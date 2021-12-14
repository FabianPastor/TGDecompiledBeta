package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda143 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_messages_saveGif f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda143(MediaDataController mediaDataController, TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_messages_saveGif;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$removeRecentGif$5(this.f$1, tLObject, tLRPC$TL_error);
    }
}
