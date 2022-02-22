package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$Message f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda41(MediaDataController mediaDataController, long j, TLRPC$Message tLRPC$Message) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tLRPC$Message;
    }

    public final void run() {
        this.f$0.lambda$putBotKeyboard$143(this.f$1, this.f$2);
    }
}
