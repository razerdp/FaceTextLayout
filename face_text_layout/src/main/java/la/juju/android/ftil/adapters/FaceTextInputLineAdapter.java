package la.juju.android.ftil.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;
import la.juju.android.ftil.R;
import la.juju.android.ftil.entities.FaceText;
import la.juju.android.ftil.listeners.OnFaceTextClickListener;

/**
 * Created by HelloVass on 16/1/1.
 */
public class FaceTextInputLineAdapter
    extends RecyclerView.Adapter<FaceTextInputLineAdapter.FaceTextViewHolder> implements ItemDragAdapter {

  private List<List<FaceText>> mPageFaceTextList;

  private Context mContext;

  private LayoutInflater mInflater;

  private OnFaceTextClickListener mOnFaceTextClickListener;

  private ItemTouchHelper mHelper;

  public FaceTextInputLineAdapter(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
  }

  @Override public FaceTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new FaceTextViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.listitem_face_text_input, parent, false));
  }

  private int curListPos=0;
  @Override public void onBindViewHolder(final FaceTextViewHolder holder, int position) {

    List<FaceText> lineFaceTextList = mPageFaceTextList.get(position);
    curListPos=position;


    for (int i = 0; i < lineFaceTextList.size(); i++) {
      final FaceText faceText = lineFaceTextList.get(i);
      TextView faceTextView = (TextView) mInflater.inflate(R.layout.view_face_text, null);
      LinearLayout.LayoutParams layoutParams = generateFaceTextContainerLayoutParams();
      holder.mLineContainer.addView(faceTextView, layoutParams);
      faceTextView.setText(faceText.content);
      faceTextView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (mOnFaceTextClickListener != null) {
            mOnFaceTextClickListener.onFaceTextClick(faceText);
          }
        }
      });
      faceTextView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
              if (mHelper!=null){
                  mHelper.startDrag(holder);
              }
              return false;
          }
      });
    }
  }

  private LinearLayout.LayoutParams generateFaceTextContainerLayoutParams() {
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
        mContext.getResources().getDimensionPixelOffset(R.dimen.face_text_view_height), 1.0f);
    layoutParams.leftMargin =
        mContext.getResources().getDimensionPixelOffset(R.dimen.face_text_view_left_margin);
    layoutParams.rightMargin =
        mContext.getResources().getDimensionPixelOffset(R.dimen.face_text_view_right_margin);
    return layoutParams;
  }

  @Override public int getItemCount() {
    return mPageFaceTextList.size();
  }

  public void setPageFaceTextList(List<List<FaceText>> pageFaceTextList) {
    mPageFaceTextList = pageFaceTextList;
  }

    //=============================================================拖拽相关
    @Override
    public int getCurListPosition() {
        return curListPos;
    }

    @Override
    public void setItemTouchHelper(ItemTouchHelper helper) {
        this.mHelper=helper;
    }

    @Override
    public void onItemMove(int curListPosition, int fromPosition, int toPosition) {
        if (mPageFaceTextList==null||mPageFaceTextList.size()==0)return;
        Collections.swap(mPageFaceTextList.get(curListPosition),fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public boolean onItemDismiss(int position) {

        return false;
    }



    public static class FaceTextViewHolder extends RecyclerView.ViewHolder implements ItemDragViewHolder {

    public LinearLayout mLineContainer;

    public FaceTextViewHolder(View itemView) {
      super(itemView);
      mLineContainer = (LinearLayout) itemView.findViewById(R.id.ll_line_container);
    }

    @Override
    public void onItemSelected() {
        mLineContainer.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onItemRelease() {
        mLineContainer.setBackgroundColor(0);
    }
  }

  public void setOnFaceTextClickListener(OnFaceTextClickListener onFaceTextClickListener) {
    mOnFaceTextClickListener = onFaceTextClickListener;
  }
}
