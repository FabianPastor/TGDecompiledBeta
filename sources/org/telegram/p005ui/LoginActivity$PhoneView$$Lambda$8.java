package org.telegram.p005ui;

import android.os.Bundle;
import org.telegram.p005ui.LoginActivity.PhoneView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_sendCode;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$PhoneView$$Lambda$8 */
final /* synthetic */ class LoginActivity$PhoneView$$Lambda$8 implements Runnable {
    private final PhoneView arg$1;
    private final TL_error arg$2;
    private final Bundle arg$3;
    private final TLObject arg$4;
    private final TL_auth_sendCode arg$5;

    LoginActivity$PhoneView$$Lambda$8(PhoneView phoneView, TL_error tL_error, Bundle bundle, TLObject tLObject, TL_auth_sendCode tL_auth_sendCode) {
        this.arg$1 = phoneView;
        this.arg$2 = tL_error;
        this.arg$3 = bundle;
        this.arg$4 = tLObject;
        this.arg$5 = tL_auth_sendCode;
    }

    public void run() {
        this.arg$1.lambda$null$7$LoginActivity$PhoneView(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
