package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.CancelAccountDeletionActivity;

public final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ CancelAccountDeletionActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.TL_auth_resendCode f$4;

    public /* synthetic */ CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(CancelAccountDeletionActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, Bundle bundle, TLObject tLObject, TLRPC.TL_auth_resendCode tL_auth_resendCode) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = tL_auth_resendCode;
    }

    public final void run() {
        this.f$0.m1515xe95e3721(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
