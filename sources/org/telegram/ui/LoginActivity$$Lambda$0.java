package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LoginActivity$$Lambda$0 implements OnClickListener {
    private final LoginActivity arg$1;
    private final boolean arg$2;
    private final String arg$3;

    LoginActivity$$Lambda$0(LoginActivity loginActivity, boolean z, String str) {
        this.arg$1 = loginActivity;
        this.arg$2 = z;
        this.arg$3 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$needShowInvalidAlert$0$LoginActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
