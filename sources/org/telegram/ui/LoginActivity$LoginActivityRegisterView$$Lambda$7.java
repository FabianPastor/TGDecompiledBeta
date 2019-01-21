package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$7 implements OnClickListener {
    private final LoginActivityRegisterView arg$1;

    LoginActivity$LoginActivityRegisterView$$Lambda$7(LoginActivityRegisterView loginActivityRegisterView) {
        this.arg$1 = loginActivityRegisterView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onBackPressed$10$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
    }
}
