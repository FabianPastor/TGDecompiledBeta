package org.telegram.messenger.support.util;

import org.telegram.messenger.support.widget.RecyclerView.Adapter;

public final class AdapterListUpdateCallback implements ListUpdateCallback {
    private final Adapter mAdapter;

    public AdapterListUpdateCallback(Adapter adapter) {
        this.mAdapter = adapter;
    }

    public void onInserted(int i, int i2) {
        this.mAdapter.notifyItemRangeInserted(i, i2);
    }

    public void onRemoved(int i, int i2) {
        this.mAdapter.notifyItemRangeRemoved(i, i2);
    }

    public void onMoved(int i, int i2) {
        this.mAdapter.notifyItemMoved(i, i2);
    }

    public void onChanged(int i, int i2, Object obj) {
        this.mAdapter.notifyItemRangeChanged(i, i2, obj);
    }
}
