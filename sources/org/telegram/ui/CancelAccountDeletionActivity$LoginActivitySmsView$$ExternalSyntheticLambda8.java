package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.CancelAccountDeletionActivity;

public final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ CancelAccountDeletionActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_account_confirmPhone f$1;

    public /* synthetic */ CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(CancelAccountDeletionActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_account_confirmPhone tL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_account_confirmPhone;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1512xedd2df4e(this.f$1, tLObject, tL_error);
    }
}
