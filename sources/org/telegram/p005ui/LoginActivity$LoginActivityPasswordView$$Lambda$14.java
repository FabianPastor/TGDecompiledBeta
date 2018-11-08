package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.LoginActivity.LoginActivityPasswordView;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$Lambda$14 */
final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$Lambda$14 implements OnClickListener {
    private final LoginActivityPasswordView arg$1;
    private final TL_auth_passwordRecovery arg$2;

    LoginActivity$LoginActivityPasswordView$$Lambda$14(LoginActivityPasswordView loginActivityPasswordView, TL_auth_passwordRecovery tL_auth_passwordRecovery) {
        this.arg$1 = loginActivityPasswordView;
        this.arg$2 = tL_auth_passwordRecovery;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$LoginActivity$LoginActivityPasswordView(this.arg$2, dialogInterface, i);
    }
}
