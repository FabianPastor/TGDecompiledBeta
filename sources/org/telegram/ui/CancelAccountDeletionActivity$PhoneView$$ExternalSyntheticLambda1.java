package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.CancelAccountDeletionActivity;

public final /* synthetic */ class CancelAccountDeletionActivity$PhoneView$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ CancelAccountDeletionActivity.PhoneView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC.TL_account_sendConfirmPhoneCode f$2;

    public /* synthetic */ CancelAccountDeletionActivity$PhoneView$$ExternalSyntheticLambda1(CancelAccountDeletionActivity.PhoneView phoneView, Bundle bundle, TLRPC.TL_account_sendConfirmPhoneCode tL_account_sendConfirmPhoneCode) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = tL_account_sendConfirmPhoneCode;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1524xa2cd87c5(this.f$1, this.f$2, tLObject, tL_error);
    }
}
