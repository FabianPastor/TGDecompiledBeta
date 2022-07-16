package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda32 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$1;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda32(StickersAlert stickersAlert, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers) {
        this.f$0 = stickersAlert;
        this.f$1 = tLRPC$TL_messages_getAttachedStickers;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
