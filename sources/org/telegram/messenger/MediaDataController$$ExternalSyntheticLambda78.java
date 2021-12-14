package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda78 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda78(MediaDataController mediaDataController, TLRPC$Message tLRPC$Message, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$Message;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$loadBotKeyboard$130(this.f$1, this.f$2);
    }
}
