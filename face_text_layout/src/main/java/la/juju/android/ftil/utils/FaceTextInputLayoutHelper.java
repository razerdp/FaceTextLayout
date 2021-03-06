package la.juju.android.ftil.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import la.juju.android.ftil.R;
import la.juju.android.ftil.adapters.FaceTextInputLineAdapter;
import la.juju.android.ftil.entities.FaceText;
import la.juju.android.ftil.listeners.OnFaceTextClickListener;
import la.juju.android.ftil.source.FaceTextProvider;

/**
 * Created by HelloVass on 16/2/24.
 */
public class FaceTextInputLayoutHelper {

  // 每页的最大行号
  public static final int PAGE_MAX_LINE_NUM = 3;
  // 每页的最大列数
  public static final int PAGE_MAX_COLUMN_COUNT = 4;

  private static FaceTextInputLayoutHelper sFaceTextInputLayoutHelper;

  private Context mContext;

  // “颜文字source”接口
  private FaceTextProvider mFaceTextProvider;

  private List<FaceTextInputLineAdapter> mFaceTextInputLineAdapterList;

  private FaceTextInputLayoutHelper(Context context) {
    mContext = context;
    mFaceTextInputLineAdapterList = new ArrayList<>();
  }

  public static FaceTextInputLayoutHelper getInstance(Context context) {
    if (sFaceTextInputLayoutHelper == null) {
      sFaceTextInputLayoutHelper = new FaceTextInputLayoutHelper(context);
    }
    return new FaceTextInputLayoutHelper(context);
  }

  /**
   * 生成所有“颜文字”页面
   */
  public List<RecyclerView> generateAllPage() {
    List<List<List<FaceText>>> allPageFaceTextList = getAllPageFaceTextList();
    List<RecyclerView> pageList = new ArrayList<>();
    for (int i = 0; i < allPageFaceTextList.size(); i++) {
      RecyclerView eachPage = generateEachPage(allPageFaceTextList.get(i));
      pageList.add(eachPage);
    }
    return pageList;
  }

  /**
   * 注册颜文字点击事件监听器
   */
  public void register(OnFaceTextClickListener listener) {
    for (FaceTextInputLineAdapter adapter : mFaceTextInputLineAdapterList) {
      adapter.setOnFaceTextClickListener(listener);
    }
  }

  /**
   * 解绑颜文字点击事件监听器
   */
  public void unregister() {
    for (FaceTextInputLineAdapter adapter : mFaceTextInputLineAdapterList) {
      adapter.setOnFaceTextClickListener(null);
    }
  }

  /**
   * 生成每个“颜文字”页面
   */
  private RecyclerView generateEachPage(List<List<FaceText>> faceTextList) {
    RecyclerView recyclerView = new RecyclerView(mContext);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    FaceTextInputLineAdapter faceTextInputLineAdapter = new FaceTextInputLineAdapter(mContext);
    mFaceTextInputLineAdapterList.add(faceTextInputLineAdapter);
    faceTextInputLineAdapter.setPageFaceTextList(faceTextList);
    recyclerView.setAdapter(faceTextInputLineAdapter);
    return recyclerView;
  }

  /**
   * 颜文字排版算法
   */
  private List<List<List<FaceText>>> getAllPageFaceTextList() {
    List<FaceText> faceTextList = mFaceTextProvider.provideFaceTextList();
    List<List<List<FaceText>>> allPageFaceTextList = new ArrayList<>();

    // 当前行数
    int currentLineNum = 0;
    // 列数
    int columnCount = 0;
    // 行的长度
    int lineWidth = 0;

    // 每页的 List
    List<List<FaceText>> pageFaceTextList = new ArrayList<>();
    // 每行的 List
    List<FaceText> lineFaceTextList = new ArrayList<>();
    // 保存每行所有“item宽度”的 List
    List<Integer> lineItemWidthList = new ArrayList<>(PAGE_MAX_COLUMN_COUNT);
    // 将当前行添加到当前页
    pageFaceTextList.add(lineFaceTextList);
    // 将当前页添加到“页List”中
    allPageFaceTextList.add(pageFaceTextList);

    TextView faceTextView = getFaceTextView();
    for (int i = 0; i < faceTextList.size(); i++) {
      FaceText faceText = faceTextList.get(i);
      int itemWidth = measureFaceTextWidth(faceTextView, faceText) + generateHorizontalMargin();
      lineWidth += itemWidth;
      columnCount++;
      lineItemWidthList.add(itemWidth);

      if (canPlaceMutipileItems(lineWidth, lineItemWidthList, columnCount) || canPlaceSingleItem(
          columnCount, lineWidth)) {
        lineFaceTextList.add(faceText);
      } else {
        currentLineNum++;
        lineWidth = itemWidth;
        columnCount = 1;

        // 切换到下一个页面
        if (currentLineNum > PAGE_MAX_LINE_NUM) {
          currentLineNum = 0;
          pageFaceTextList = new ArrayList<>();
          allPageFaceTextList.add(pageFaceTextList);
        }

        lineItemWidthList = new ArrayList<>();
        lineItemWidthList.add(itemWidth);

        lineFaceTextList = new ArrayList<>();
        lineFaceTextList.add(faceText);
        pageFaceTextList.add(lineFaceTextList);
      }
    }

    return allPageFaceTextList;
  }

  /**
   * 能否在单行中摆放
   *
   * @param columnCount 列数
   * @param lineWidth 行宽
   */
  private boolean canPlaceSingleItem(int columnCount, int lineWidth) {
    return columnCount == 1 && lineWidth > ScreenUtil.getScreenWidth(mContext);
  }

  /**
   * 能否在一行中摆放多个 item
   */
  private boolean canPlaceMutipileItems(int lineWidth, List<Integer> lineItemWidthList,
      int columnCount) {

    for (int itemWidth : lineItemWidthList)
      if (itemWidth > ScreenUtil.getScreenWidth(mContext) / columnCount) return false;

    return lineWidth <= ScreenUtil.getScreenWidth(mContext) && columnCount <= PAGE_MAX_COLUMN_COUNT;
  }

  /**
   * 获取一个 TextView
   */
  private TextView getFaceTextView() {
    return (TextView) LayoutInflater.from(mContext)
        .inflate(R.layout.wrapper_face_text, null)
        .findViewById(R.id.tv_face_text);
  }

  /**
   * 测量 颜文字 的长度
   */
  private int measureFaceTextWidth(TextView faceTextView, FaceText faceText) {
    if (faceTextView == null || faceText == null) {
      return 0;
    }
    faceTextView.setText(faceText.content);
    faceTextView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    return faceTextView.getMeasuredWidth();
  }

  /**
   * 生成 leftMargin 和 rightMargin
   */
  private int generateHorizontalMargin() {
    int leftMargin =
        mContext.getResources().getDimensionPixelOffset(R.dimen.face_text_view_left_margin);
    int rightMargin =
        mContext.getResources().getDimensionPixelOffset(R.dimen.face_text_view_right_margin);
    return leftMargin + rightMargin;
  }

  public void setFaceTextProvider(FaceTextProvider provider) {
    mFaceTextProvider = provider;
  }

  public FaceTextProvider getFaceTextProvider() {
    return mFaceTextProvider;
  }
}
