package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda16 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda16(PassportActivity.AnonymousClass8 r1, boolean z) {
        this.f$0 = r1;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$run$10(this.f$1, tLObject, tLRPC$TL_error);
    }
}
