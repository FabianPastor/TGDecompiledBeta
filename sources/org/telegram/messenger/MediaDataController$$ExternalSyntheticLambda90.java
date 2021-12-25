package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda90 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda90(MediaDataController mediaDataController, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$loadStickersByEmojiOrName$45(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
