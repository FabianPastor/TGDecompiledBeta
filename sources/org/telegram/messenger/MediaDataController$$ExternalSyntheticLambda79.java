package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda79 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda79(MediaDataController mediaDataController, TLRPC$Message tLRPC$Message, String str) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$Message;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$verifyAnimatedStickerMessage$33(this.f$1, this.f$2);
    }
}