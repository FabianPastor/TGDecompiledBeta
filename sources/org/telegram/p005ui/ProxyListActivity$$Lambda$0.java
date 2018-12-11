package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ProxyListActivity$$Lambda$0 */
final /* synthetic */ class ProxyListActivity$$Lambda$0 implements OnItemClickListener {
    private final ProxyListActivity arg$1;

    ProxyListActivity$$Lambda$0(ProxyListActivity proxyListActivity) {
        this.arg$1 = proxyListActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$ProxyListActivity(view, i);
    }
}
