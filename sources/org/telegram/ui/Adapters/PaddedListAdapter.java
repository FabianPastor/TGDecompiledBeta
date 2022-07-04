package org.telegram.ui.Adapters;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.ui.Components.RecyclerListView;

public class PaddedListAdapter extends RecyclerListView.SelectionAdapter {
    private final int PADDING_VIEW_TYPE = -983904;
    private GetPaddingRunnable getPaddingRunnable;
    private int lastPadding;
    private RecyclerView.AdapterDataObserver mDataObserver;
    private Integer padding = null;
    public View paddingView;
    public boolean paddingViewAttached = false;
    private RecyclerListView.SelectionAdapter wrappedAdapter;

    public interface GetPaddingRunnable {
        int run(int i);
    }

    public PaddedListAdapter(RecyclerListView.SelectionAdapter adapter) {
        AnonymousClass2 r0 = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                super.onChanged();
                PaddedListAdapter.this.notifyDataSetChanged();
            }

            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(positionStart + 1, itemCount);
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeInserted(positionStart + 1, itemCount);
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeRemoved(positionStart + 1, itemCount);
            }

            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(fromPosition + 1, toPosition + 1 + itemCount);
            }
        };
        this.mDataObserver = r0;
        this.wrappedAdapter = adapter;
        adapter.registerAdapterDataObserver(r0);
    }

    public PaddedListAdapter(RecyclerListView.SelectionAdapter adapter, GetPaddingRunnable getPaddingRunnable2) {
        AnonymousClass2 r0 = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                super.onChanged();
                PaddedListAdapter.this.notifyDataSetChanged();
            }

            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(positionStart + 1, itemCount);
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeInserted(positionStart + 1, itemCount);
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeRemoved(positionStart + 1, itemCount);
            }

            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(fromPosition + 1, toPosition + 1 + itemCount);
            }
        };
        this.mDataObserver = r0;
        this.wrappedAdapter = adapter;
        adapter.registerAdapterDataObserver(r0);
        this.getPaddingRunnable = getPaddingRunnable2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        if (holder.getAdapterPosition() == 0) {
            return false;
        }
        return this.wrappedAdapter.isEnabled(holder);
    }

    public void setPadding(int padding2) {
        this.padding = Integer.valueOf(padding2);
        View view = this.paddingView;
        if (view != null) {
            view.requestLayout();
        }
    }

    public void setPadding(GetPaddingRunnable getPaddingRunnable2) {
        this.getPaddingRunnable = getPaddingRunnable2;
        View view = this.paddingView;
        if (view != null) {
            view.requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public int getPadding(int parentHeight) {
        Integer num = this.padding;
        if (num != null) {
            int intValue = num.intValue();
            this.lastPadding = intValue;
            return intValue;
        }
        GetPaddingRunnable getPaddingRunnable2 = this.getPaddingRunnable;
        if (getPaddingRunnable2 != null) {
            int run = getPaddingRunnable2.run(parentHeight);
            this.lastPadding = run;
            return run;
        }
        this.lastPadding = 0;
        return 0;
    }

    public int getPadding() {
        return this.lastPadding;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != -983904) {
            return this.wrappedAdapter.onCreateViewHolder(parent, viewType);
        }
        AnonymousClass1 r1 = new View(parent.getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(PaddedListAdapter.this.getPadding(((View) getParent()).getMeasuredHeight()), NUM));
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                PaddedListAdapter.this.paddingViewAttached = true;
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                PaddedListAdapter.this.paddingViewAttached = false;
            }
        };
        this.paddingView = r1;
        return new RecyclerListView.Holder(r1);
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return -983904;
        }
        return this.wrappedAdapter.getItemViewType(position - 1);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > 0) {
            this.wrappedAdapter.onBindViewHolder(holder, position - 1);
        }
    }

    public int getItemCount() {
        return this.wrappedAdapter.getItemCount() + 1;
    }
}
