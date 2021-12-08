package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_messages_getAttachedStickers f$3;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda16(StickersAlert stickersAlert, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers) {
        this.f$0 = stickersAlert;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_getAttachedStickers;
    }

    public final void run() {
        this.f$0.m2641lambda$new$0$orgtelegramuiComponentsStickersAlert(this.f$1, this.f$2, this.f$3);
    }
}
