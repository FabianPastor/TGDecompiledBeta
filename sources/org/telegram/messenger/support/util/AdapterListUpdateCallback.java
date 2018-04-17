package org.telegram.messenger.support.util;

import org.telegram.messenger.support.widget.RecyclerView.Adapter;

public final class AdapterListUpdateCallback implements ListUpdateCallback {
    private final Adapter mAdapter;

    public AdapterListUpdateCallback(Adapter adapter) {
        this.mAdapter = adapter;
    }

    public void onInserted(int position, int count) {
        this.mAdapter.notifyItemRangeInserted(position, count);
    }

    public void onRemoved(int position, int count) {
        this.mAdapter.notifyItemRangeRemoved(position, count);
    }

    public void onMoved(int fromPosition, int toPosition) {
        this.mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    public void onChanged(int position, int count, Object payload) {
        this.mAdapter.notifyItemRangeChanged(position, count, payload);
    }
}
