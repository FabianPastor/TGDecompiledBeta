package org.telegram.ui.Adapters;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.ui.Components.RecyclerListView;

public class PaddedListAdapter extends RecyclerListView.SelectionAdapter {
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

    public PaddedListAdapter(RecyclerListView.SelectionAdapter selectionAdapter) {
        AnonymousClass2 r0 = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                super.onChanged();
                PaddedListAdapter.this.notifyDataSetChanged();
            }

            public void onItemRangeChanged(int i, int i2) {
                super.onItemRangeChanged(i, i2);
                PaddedListAdapter.this.notifyItemRangeChanged(i + 1, i2);
            }

            public void onItemRangeInserted(int i, int i2) {
                super.onItemRangeInserted(i, i2);
                PaddedListAdapter.this.notifyItemRangeInserted(i + 1, i2);
            }

            public void onItemRangeRemoved(int i, int i2) {
                super.onItemRangeRemoved(i, i2);
                PaddedListAdapter.this.notifyItemRangeRemoved(i + 1, i2);
            }

            public void onItemRangeMoved(int i, int i2, int i3) {
                super.onItemRangeMoved(i, i2, i3);
                PaddedListAdapter.this.notifyItemRangeChanged(i + 1, i2 + 1 + i3);
            }
        };
        this.mDataObserver = r0;
        this.wrappedAdapter = selectionAdapter;
        selectionAdapter.registerAdapterDataObserver(r0);
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getAdapterPosition() == 0) {
            return false;
        }
        return this.wrappedAdapter.isEnabled(viewHolder);
    }

    public void setPadding(int i) {
        this.padding = Integer.valueOf(i);
        View view = this.paddingView;
        if (view != null) {
            view.requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public int getPadding(int i) {
        Integer num = this.padding;
        if (num != null) {
            int intValue = num.intValue();
            this.lastPadding = intValue;
            return intValue;
        }
        GetPaddingRunnable getPaddingRunnable2 = this.getPaddingRunnable;
        if (getPaddingRunnable2 != null) {
            int run = getPaddingRunnable2.run(i);
            this.lastPadding = run;
            return run;
        }
        this.lastPadding = 0;
        return 0;
    }

    public int getPadding() {
        return this.lastPadding;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i != -983904) {
            return this.wrappedAdapter.onCreateViewHolder(viewGroup, i);
        }
        AnonymousClass1 r0 = new View(viewGroup.getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(PaddedListAdapter.this.getPadding(((View) getParent()).getMeasuredHeight()), NUM));
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
        this.paddingView = r0;
        return new RecyclerListView.Holder(r0);
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return -983904;
        }
        return this.wrappedAdapter.getItemViewType(i - 1);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (i > 0) {
            this.wrappedAdapter.onBindViewHolder(viewHolder, i - 1);
        }
    }

    public int getItemCount() {
        return this.wrappedAdapter.getItemCount() + 1;
    }
}
