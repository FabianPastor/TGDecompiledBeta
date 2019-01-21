package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ProxyListActivity$$Lambda$1 implements OnItemLongClickListener {
    private final ProxyListActivity arg$1;

    ProxyListActivity$$Lambda$1(ProxyListActivity proxyListActivity) {
        this.arg$1 = proxyListActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$ProxyListActivity(view, i);
    }
}
