package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class StickersAlert$$Lambda$14 implements RequestDelegate {
    private final StickersAlert arg$1;

    StickersAlert$$Lambda$14(StickersAlert stickersAlert) {
        this.arg$1 = stickersAlert;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$14$StickersAlert(tLObject, tL_error);
    }
}
