package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacyUsersActivity$8Pgo4HS6PGjX-d7DerbhGOGCfXc implements OnClickListener {
    private final /* synthetic */ PrivacyUsersActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$PrivacyUsersActivity$8Pgo4HS6PGjX-d7DerbhGOGCfXc(PrivacyUsersActivity privacyUsersActivity, int i) {
        this.f$0 = privacyUsersActivity;
        this.f$1 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showUnblockAlert$3$PrivacyUsersActivity(this.f$1, dialogInterface, i);
    }
}
