package org.telegram.p005ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;

/* renamed from: org.telegram.ui.Components.StickersAlert$$Lambda$1 */
final /* synthetic */ class StickersAlert$$Lambda$1 implements RequestDelegate {
    private final StickersAlert arg$1;
    private final Object arg$2;
    private final TL_messages_getAttachedStickers arg$3;
    private final RequestDelegate arg$4;

    StickersAlert$$Lambda$1(StickersAlert stickersAlert, Object obj, TL_messages_getAttachedStickers tL_messages_getAttachedStickers, RequestDelegate requestDelegate) {
        this.arg$1 = stickersAlert;
        this.arg$2 = obj;
        this.arg$3 = tL_messages_getAttachedStickers;
        this.arg$4 = requestDelegate;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$new$2$StickersAlert(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
