package org.telegram.p005ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Components.StickersAlert$$Lambda$2 */
final /* synthetic */ class StickersAlert$$Lambda$2 implements RequestDelegate {
    private final StickersAlert arg$1;

    StickersAlert$$Lambda$2(StickersAlert stickersAlert) {
        this.arg$1 = stickersAlert;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadStickerSet$4$StickersAlert(tLObject, tL_error);
    }
}
