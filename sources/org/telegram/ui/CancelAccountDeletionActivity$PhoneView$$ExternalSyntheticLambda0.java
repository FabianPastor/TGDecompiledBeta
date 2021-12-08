package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.CancelAccountDeletionActivity;

public final /* synthetic */ class CancelAccountDeletionActivity$PhoneView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ CancelAccountDeletionActivity.PhoneView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.TL_account_sendConfirmPhoneCode f$4;

    public /* synthetic */ CancelAccountDeletionActivity$PhoneView$$ExternalSyntheticLambda0(CancelAccountDeletionActivity.PhoneView phoneView, TLRPC.TL_error tL_error, Bundle bundle, TLObject tLObject, TLRPC.TL_account_sendConfirmPhoneCode tL_account_sendConfirmPhoneCode) {
        this.f$0 = phoneView;
        this.f$1 = tL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = tL_account_sendConfirmPhoneCode;
    }

    public final void run() {
        this.f$0.m1523x7d397ec4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
