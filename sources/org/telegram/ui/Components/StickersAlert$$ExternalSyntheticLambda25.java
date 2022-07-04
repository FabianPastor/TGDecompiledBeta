package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$3;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda25(StickersAlert stickersAlert, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers) {
        this.f$0 = stickersAlert;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_messages_getAttachedStickers;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2, this.f$3);
    }
}
