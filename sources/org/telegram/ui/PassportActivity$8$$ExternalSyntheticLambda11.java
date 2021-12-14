package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda11(PassportActivity.AnonymousClass8 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$generateNewSecret$7(tLObject, tLRPC$TL_error);
    }
}
