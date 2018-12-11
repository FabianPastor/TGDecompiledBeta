package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.PrivacyUsersActivity$$Lambda$0 */
final /* synthetic */ class PrivacyUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final PrivacyUsersActivity arg$1;

    PrivacyUsersActivity$$Lambda$0(PrivacyUsersActivity privacyUsersActivity) {
        this.arg$1 = privacyUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$PrivacyUsersActivity(view, i);
    }
}
