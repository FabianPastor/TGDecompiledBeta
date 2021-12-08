package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda23 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ TLRPC.TL_messages_getAttachedStickers f$1;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda23(StickersAlert stickersAlert, TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers) {
        this.f$0 = stickersAlert;
        this.f$1 = tL_messages_getAttachedStickers;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2642lambda$new$1$orgtelegramuiComponentsStickersAlert(this.f$1, tLObject, tL_error);
    }
}
