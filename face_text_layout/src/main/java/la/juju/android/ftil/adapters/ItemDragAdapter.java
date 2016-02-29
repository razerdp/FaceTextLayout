package la.juju.android.ftil.adapters;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by 大灯泡 on 2016/2/29.
 */
public interface ItemDragAdapter {
    void onItemMove(int curListPosition,int fromPosition, int toPosition);
    boolean onItemDismiss(int position);
    int getCurListPosition();
    void setItemTouchHelper(ItemTouchHelper helper);
}
