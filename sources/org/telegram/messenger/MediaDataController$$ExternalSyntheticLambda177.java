package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda177 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC$TL_messages_saveRecentSticker f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda177(MediaDataController mediaDataController, Object obj, TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker) {
        this.f$0 = mediaDataController;
        this.f$1 = obj;
        this.f$2 = tLRPC$TL_messages_saveRecentSticker;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$addRecentSticker$19(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
