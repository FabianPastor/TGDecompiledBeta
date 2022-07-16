package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda28 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda28(StickersAlert stickersAlert) {
        this.f$0 = stickersAlert;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$updateFields$16(tLObject, tLRPC$TL_error);
    }
}
