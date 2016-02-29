package la.juju.android.ftil.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import la.juju.android.ftil.adapters.ItemDragAdapter;
import la.juju.android.ftil.adapters.ItemDragViewHolder;

/**
 * Created by 大灯泡 on 2016/2/29.
 */
public class ItemTouchCallBack extends ItemTouchHelper.Callback {
    private boolean needSwipe = false;
    private ItemDragAdapter mItemDragAdapter;

    public ItemTouchCallBack(ItemDragAdapter itemDragAdapter) {
        mItemDragAdapter = itemDragAdapter;
    }
    public ItemTouchCallBack(ItemDragAdapter itemDragAdapter,boolean needSwip) {
        mItemDragAdapter = itemDragAdapter;
        this.needSwipe=needSwip;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        int swipeFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            //上下左右支持
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            //gridview滑动删除无需支持
            swipeFlags = 0;
        }
        else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            swipeFlags=0;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (mItemDragAdapter == null) return false;
        if (viewHolder.getItemViewType()!=target.getItemViewType())return false;
        mItemDragAdapter.onItemMove(mItemDragAdapter.getCurListPosition(), viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (!needSwipe || mItemDragAdapter == null) return;
        mItemDragAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**Item(长按)激活时*/
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder instanceof ItemDragViewHolder && actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ((ItemDragViewHolder) viewHolder).onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**Item松手释放时*/
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ItemDragViewHolder) {
            ((ItemDragViewHolder) viewHolder).onItemRelease();
        }
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return needSwipe;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    //=============================================================Getter/Setter

    public boolean isNeedSwipe() {
        return needSwipe;
    }

    public void setNeedSwipe(boolean needSwipe) {
        this.needSwipe = needSwipe;
    }
}
