package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class PrivacyUsersActivity$$Lambda$0 implements OnItemClickListener {
    private final PrivacyUsersActivity arg$1;

    PrivacyUsersActivity$$Lambda$0(PrivacyUsersActivity privacyUsersActivity) {
        this.arg$1 = privacyUsersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$PrivacyUsersActivity(view, i);
    }
}
