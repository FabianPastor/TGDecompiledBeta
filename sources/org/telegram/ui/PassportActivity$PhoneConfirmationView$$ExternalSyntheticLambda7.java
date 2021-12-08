package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ PassportActivity.PhoneConfirmationView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_account_verifyPhone f$2;

    public /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda7(PassportActivity.PhoneConfirmationView phoneConfirmationView, TLRPC.TL_error tL_error, TLRPC.TL_account_verifyPhone tL_account_verifyPhone) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tL_error;
        this.f$2 = tL_account_verifyPhone;
    }

    public final void run() {
        this.f$0.m3455xdabb2b7a(this.f$1, this.f$2);
    }
}
