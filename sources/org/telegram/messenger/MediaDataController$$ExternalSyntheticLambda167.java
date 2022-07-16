package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda167 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda167(MediaDataController mediaDataController, String str, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = str;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadStickersByEmojiOrName$63(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
