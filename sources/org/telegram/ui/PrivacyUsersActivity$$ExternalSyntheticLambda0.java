package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class PrivacyUsersActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PrivacyUsersActivity f$0;
    public final /* synthetic */ Integer f$1;

    public /* synthetic */ PrivacyUsersActivity$$ExternalSyntheticLambda0(PrivacyUsersActivity privacyUsersActivity, Integer num) {
        this.f$0 = privacyUsersActivity;
        this.f$1 = num;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showUnblockAlert$3(this.f$1, dialogInterface, i);
    }
}
