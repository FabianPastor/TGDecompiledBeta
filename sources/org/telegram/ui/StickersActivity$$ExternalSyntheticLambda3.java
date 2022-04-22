package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class StickersActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ StickersActivity f$0;

    public /* synthetic */ StickersActivity$$ExternalSyntheticLambda3(StickersActivity stickersActivity) {
        this.f$0 = stickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendReorder$5(tLObject, tLRPC$TL_error);
    }
}
