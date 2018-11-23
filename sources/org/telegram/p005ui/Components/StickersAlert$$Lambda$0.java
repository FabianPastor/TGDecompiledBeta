package org.telegram.p005ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;

/* renamed from: org.telegram.ui.Components.StickersAlert$$Lambda$0 */
final /* synthetic */ class StickersAlert$$Lambda$0 implements RequestDelegate {
    private final StickersAlert arg$1;
    private final TL_messages_getAttachedStickers arg$2;

    StickersAlert$$Lambda$0(StickersAlert stickersAlert, TL_messages_getAttachedStickers tL_messages_getAttachedStickers) {
        this.arg$1 = stickersAlert;
        this.arg$2 = tL_messages_getAttachedStickers;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$new$1$StickersAlert(this.arg$2, tLObject, tL_error);
    }
}
