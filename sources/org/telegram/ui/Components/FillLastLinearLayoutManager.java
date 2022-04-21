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

    public FillLastLinearLayoutManager(Context context, int h, RecyclerView recyclerView) {
        super(context);
        this.additionalHeight = h;
    }

    public FillLastLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int h, RecyclerView recyclerView) {
        super(context, orientation, reverseLayout);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public void setAdditionalHeight(int value) {
        this.additionalHeight = value;
        calcLastItemHeight();
    }

    public void setSkipFirstItem() {
        this.skipFirstItem = true;
    }

    public void setBind(boolean value) {
        this.bind = value;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    private void calcLastItemHeight() {
        RecyclerView.Adapter adapter;
        if (this.listHeight > 0 && (adapter = this.listView.getAdapter()) != null) {
            int count = adapter.getItemCount() - 1;
            int allHeight = 0;
            for (int a = this.skipFirstItem; a < count; a++) {
                int type = adapter.getItemViewType(a);
                RecyclerView.ViewHolder holder = this.heights.get(type, (Object) null);
                if (holder == null) {
                    holder = adapter.createViewHolder(this.listView, type);
                    this.heights.put(type, holder);
                    if (holder.itemView.getLayoutParams() == null) {
                        holder.itemView.setLayoutParams(generateDefaultLayoutParams());
                    }
                }
                if (this.bind) {
                    adapter.onBindViewHolder(holder, a);
                }
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                holder.itemView.measure(getChildMeasureSpec(this.listWidth, getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width, canScrollHorizontally()), getChildMeasureSpec(this.listHeight, getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height, canScrollVertically()));
                allHeight += holder.itemView.getMeasuredHeight();
                if (allHeight >= this.listHeight) {
                    break;
                }
            }
            this.lastItemHeight = Math.max(0, ((this.listHeight - allHeight) - this.additionalHeight) - this.listView.getPaddingBottom());
        }
    }

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int lastHeight = this.listHeight;
        this.listWidth = View.MeasureSpec.getSize(widthSpec);
        int size = View.MeasureSpec.getSize(heightSpec);
        this.listHeight = size;
        if (lastHeight != size) {
            calcLastItemHeight();
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        this.heights.clear();
        calcLastItemHeight();
        super.onAdapterChanged(oldAdapter, newAdapter);
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.heights.clear();
        calcLastItemHeight();
        super.onItemsChanged(recyclerView);
    }

    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        calcLastItemHeight();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount, payload);
        calcLastItemHeight();
    }

    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        if (this.listView.findContainingViewHolder(child).getAdapterPosition() == getItemCount() - 1) {
            ((RecyclerView.LayoutParams) child.getLayoutParams()).height = Math.max(this.lastItemHeight, 0);
        }
        super.measureChildWithMargins(child, 0, 0);
    }
}
