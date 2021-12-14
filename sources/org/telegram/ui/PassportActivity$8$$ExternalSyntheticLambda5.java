package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda5(PassportActivity.AnonymousClass8 r1, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$generateNewSecret$6(this.f$1);
    }
}
