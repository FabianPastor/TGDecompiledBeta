package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LoginActivity$$Lambda$1 implements OnClickListener {
    private final LoginActivity arg$1;
    private final int arg$2;

    LoginActivity$$Lambda$1(LoginActivity loginActivity, int i) {
        this.arg$1 = loginActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$needShowProgress$1$LoginActivity(this.arg$2, dialogInterface, i);
    }
}
