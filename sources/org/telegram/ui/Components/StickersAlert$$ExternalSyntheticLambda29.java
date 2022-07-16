package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda29 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC$TL_messages_getAttachedStickers f$2;
    public final /* synthetic */ RequestDelegate f$3;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda29(StickersAlert stickersAlert, Object obj, TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers, RequestDelegate requestDelegate) {
        this.f$0 = stickersAlert;
        this.f$1 = obj;
        this.f$2 = tLRPC$TL_messages_getAttachedStickers;
        this.f$3 = requestDelegate;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$2(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
