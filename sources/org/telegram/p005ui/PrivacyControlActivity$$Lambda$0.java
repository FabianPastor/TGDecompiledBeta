package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.PrivacyControlActivity$$Lambda$0 */
final /* synthetic */ class PrivacyControlActivity$$Lambda$0 implements OnItemClickListener {
    private final PrivacyControlActivity arg$1;

    PrivacyControlActivity$$Lambda$0(PrivacyControlActivity privacyControlActivity) {
        this.arg$1 = privacyControlActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$PrivacyControlActivity(view, i);
    }
}
