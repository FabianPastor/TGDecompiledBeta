package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FillLastLinearLayoutManager extends LinearLayoutManager {
    private int additionalHeight;
    private boolean bind = true;
    private boolean canScrollVertically = true;
    private SparseArray<RecyclerView.ViewHolder> heights = new SparseArray<>();
    private int lastItemHeight = -1;
    private int listHeight;
    private RecyclerView listView;
    private int listWidth;
    private boolean skipFirstItem;

    public FillLastLinearLayoutManager(Context context, int i, boolean z, int i2, RecyclerView recyclerView) {
        super(context, i, z);
        this.listView = recyclerView;
        this.additionalHeight = i2;
    }

    public void setSkipFirstItem() {
        this.skipFirstItem = true;
    }

    public void setBind(boolean z) {
        this.bind = z;
    }

    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    private void calcLastItemHeight() {
        RecyclerView.Adapter adapter;
        if (this.listHeight > 0 && (adapter = this.listView.getAdapter()) != null) {
            int itemCount = adapter.getItemCount() - 1;
            int i = 0;
            for (int i2 = this.skipFirstItem; i2 < itemCount; i2++) {
                int itemViewType = adapter.getItemViewType(i2);
                RecyclerView.ViewHolder viewHolder = this.heights.get(itemViewType, (Object) null);
                if (viewHolder == null) {
                    viewHolder = adapter.createViewHolder(this.listView, itemViewType);
                    this.heights.put(itemViewType, viewHolder);
                    if (viewHolder.itemView.getLayoutParams() == null) {
                        viewHolder.itemView.setLayoutParams(generateDefaultLayoutParams());
                    }
                }
                if (this.bind) {
                    adapter.onBindViewHolder(viewHolder, i2);
                }
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                viewHolder.itemView.measure(RecyclerView.LayoutManager.getChildMeasureSpec(this.listWidth, getWidthMode(), getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width, canScrollHorizontally()), RecyclerView.LayoutManager.getChildMeasureSpec(this.listHeight, getHeightMode(), getPaddingTop() + getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height, canScrollVertically()));
                i += viewHolder.itemView.getMeasuredHeight();
                if (i >= this.listHeight) {
                    break;
                }
            }
            this.lastItemHeight = Math.max(0, ((this.listHeight - i) - this.additionalHeight) - this.listView.getPaddingBottom());
        }
    }

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2) {
        int i3 = this.listHeight;
        this.listWidth = View.MeasureSpec.getSize(i);
        int size = View.MeasureSpec.getSize(i2);
        this.listHeight = size;
        if (i3 != size) {
            calcLastItemHeight();
        }
        super.onMeasure(recycler, state, i, i2);
    }

    public void onAdapterChanged(RecyclerView.Adapter adapter, RecyclerView.Adapter adapter2) {
        this.heights.clear();
        calcLastItemHeight();
        super.onAdapterChanged(adapter, adapter2);
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.heights.clear();
        calcLastItemHeight();
        super.onItemsChanged(recyclerView);
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        super.onItemsAdded(recyclerView, i, i2);
        calcLastItemHeight();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        super.onItemsRemoved(recyclerView, i, i2);
        calcLastItemHeight();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        super.onItemsMoved(recyclerView, i, i2, i3);
        calcLastItemHeight();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        super.onItemsUpdated(recyclerView, i, i2);
        calcLastItemHeight();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        super.onItemsUpdated(recyclerView, i, i2, obj);
        calcLastItemHeight();
    }

    public void measureChildWithMargins(View view, int i, int i2) {
        if (this.listView.findContainingViewHolder(view).getAdapterPosition() == getItemCount() - 1) {
            ((RecyclerView.LayoutParams) view.getLayoutParams()).height = Math.max(this.lastItemHeight, 0);
        }
        super.measureChildWithMargins(view, 0, 0);
    }
}
