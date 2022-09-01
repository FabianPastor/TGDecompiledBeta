package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityEmailCodeView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Bundle f$2;

    public /* synthetic */ LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda15(LoginActivity.LoginActivityEmailCodeView loginActivityEmailCodeView, TLObject tLObject, Bundle bundle) {
        this.f$0 = loginActivityEmailCodeView;
        this.f$1 = tLObject;
        this.f$2 = bundle;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$11(this.f$1, this.f$2);
    }
}
