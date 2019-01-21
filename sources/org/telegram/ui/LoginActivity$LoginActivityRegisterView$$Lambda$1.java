package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$1 implements OnClickListener {
    private final LoginActivityRegisterView arg$1;

    LoginActivity$LoginActivityRegisterView$$Lambda$1(LoginActivityRegisterView loginActivityRegisterView) {
        this.arg$1 = loginActivityRegisterView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showTermsOfService$3$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
    }
}
