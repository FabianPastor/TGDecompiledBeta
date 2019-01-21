package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class PrivacyUsersActivity$$Lambda$1 implements OnItemLongClickListener {
    private final PrivacyUsersActivity arg$1;

    PrivacyUsersActivity$$Lambda$1(PrivacyUsersActivity privacyUsersActivity) {
        this.arg$1 = privacyUsersActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$PrivacyUsersActivity(view, i);
    }
}
