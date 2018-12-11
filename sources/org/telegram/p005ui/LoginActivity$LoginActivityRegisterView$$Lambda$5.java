package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.LoginActivity.LoginActivityRegisterView;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$Lambda$5 */
final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$5 implements OnClickListener {
    private final LoginActivityRegisterView arg$1;

    LoginActivity$LoginActivityRegisterView$$Lambda$5(LoginActivityRegisterView loginActivityRegisterView) {
        this.arg$1 = loginActivityRegisterView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onBackPressed$7$LoginActivity$LoginActivityRegisterView(dialogInterface, i);
    }
}
