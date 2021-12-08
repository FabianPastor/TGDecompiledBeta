package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FillLastGridLayoutManager extends GridLayoutManager {
    private int additionalHeight;
    private boolean bind = true;
    private boolean canScrollVertically = true;
    private SparseArray<RecyclerView.ViewHolder> heights = new SparseArray<>();
    protected int lastItemHeight = -1;
    private int listHeight;
    private RecyclerView listView;
    private int listWidth;

    public void setBind(boolean bind2) {
        this.bind = bind2;
    }

    public FillLastGridLayoutManager(Context context, int spanCount, int h, RecyclerView recyclerView) {
        super(context, spanCount);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public FillLastGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout, int h, RecyclerView recyclerView) {
        super(context, spanCount, orientation, reverseLayout);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public void setAdditionalHeight(int value) {
        this.additionalHeight = value;
        calcLastItemHeight();
    }

    /* access modifiers changed from: protected */
    public void calcLastItemHeight() {
        RecyclerView.Adapter adapter;
        int spanCounter;
        int spanCount;
        RecyclerView.Adapter adapter2;
        if (this.listHeight > 0 && shouldCalcLastItemHeight() && (adapter = this.listView.getAdapter()) != null) {
            int spanCount2 = getSpanCount();
            int spanCounter2 = 0;
            int count = adapter.getItemCount() - 1;
            int allHeight = 0;
            GridLayoutManager.SpanSizeLookup spanSizeLookup = getSpanSizeLookup();
            boolean add = true;
            int a = 0;
            while (true) {
                if (a >= count) {
                    int i = spanCount2;
                    break;
                }
                int spanSize = spanSizeLookup.getSpanSize(a);
                int spanCounter3 = spanCounter2 + spanSize;
                if (spanSize == spanCount2 || spanCounter3 > spanCount2) {
                    spanCounter3 = spanSize;
                    add = true;
                }
                if (!add) {
                    adapter2 = adapter;
                    spanCount = spanCount2;
                    spanCounter = spanCounter3;
                } else {
                    add = false;
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
                    adapter2 = adapter;
                    spanCount = spanCount2;
                    spanCounter = spanCounter3;
                    holder.itemView.measure(getChildMeasureSpec(this.listWidth, getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width, canScrollHorizontally()), getChildMeasureSpec(this.listHeight, getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height, canScrollVertically()));
                    allHeight += holder.itemView.getMeasuredHeight();
                    if (allHeight >= (this.listHeight - this.additionalHeight) - this.listView.getPaddingBottom()) {
                        int i2 = spanCounter;
                        break;
                    }
                }
                a++;
                adapter = adapter2;
                spanCount2 = spanCount;
                spanCounter2 = spanCounter;
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

    /* access modifiers changed from: protected */
    public void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        if (this.listView.findContainingViewHolder(view).getAdapterPosition() == getItemCount() - 1) {
            ((RecyclerView.LayoutParams) view.getLayoutParams()).height = Math.max(this.lastItemHeight, 0);
        }
        super.measureChild(view, otherDirParentSpecMode, alreadyMeasured);
    }

    /* access modifiers changed from: protected */
    public boolean shouldCalcLastItemHeight() {
        return true;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }
}
