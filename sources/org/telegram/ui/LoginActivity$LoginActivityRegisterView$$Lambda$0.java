package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$0 implements OnClickListener {
    private final LoginActivityRegisterView arg$1;

    LoginActivity$LoginActivityRegisterView$$Lambda$0(LoginActivityRegisterView loginActivityRegisterView) {
        this.arg$1 = loginActivityRegisterView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showTermsOfService$0$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
    }
}
