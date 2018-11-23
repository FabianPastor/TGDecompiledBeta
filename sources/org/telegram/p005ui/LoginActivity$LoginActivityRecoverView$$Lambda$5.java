package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.LoginActivity.LoginActivityRecoverView;
import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$Lambda$5 */
final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$Lambda$5 implements OnClickListener {
    private final LoginActivityRecoverView arg$1;
    private final TLObject arg$2;

    LoginActivity$LoginActivityRecoverView$$Lambda$5(LoginActivityRecoverView loginActivityRecoverView, TLObject tLObject) {
        this.arg$1 = loginActivityRecoverView;
        this.arg$2 = tLObject;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$3$LoginActivity$LoginActivityRecoverView(this.arg$2, dialogInterface, i);
    }
}
