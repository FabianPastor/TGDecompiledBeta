package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda19 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC.TL_messages_getAttachedStickers f$2;
    public final /* synthetic */ RequestDelegate f$3;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda19(StickersAlert stickersAlert, Object obj, TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers, RequestDelegate requestDelegate) {
        this.f$0 = stickersAlert;
        this.f$1 = obj;
        this.f$2 = tL_messages_getAttachedStickers;
        this.f$3 = requestDelegate;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4430lambda$new$2$orgtelegramuiComponentsStickersAlert(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
