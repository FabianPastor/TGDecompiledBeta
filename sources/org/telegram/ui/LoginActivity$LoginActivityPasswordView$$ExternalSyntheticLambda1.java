package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LoginActivity.LoginActivityPasswordView f$0;
    public final /* synthetic */ TLRPC$TL_auth_passwordRecovery f$1;

    public /* synthetic */ LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1(LoginActivity.LoginActivityPasswordView loginActivityPasswordView, TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery) {
        this.f$0 = loginActivityPasswordView;
        this.f$1 = tLRPC$TL_auth_passwordRecovery;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$new$1(this.f$1, dialogInterface, i);
    }
}
