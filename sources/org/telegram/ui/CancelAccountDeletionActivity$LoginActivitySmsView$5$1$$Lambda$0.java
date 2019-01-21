package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.5.AnonymousClass1;

final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$5$1$$Lambda$0 implements RequestDelegate {
    private final AnonymousClass1 arg$1;

    CancelAccountDeletionActivity$LoginActivitySmsView$5$1$$Lambda$0(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$CancelAccountDeletionActivity$LoginActivitySmsView$5$1(tLObject, tL_error);
    }
}
