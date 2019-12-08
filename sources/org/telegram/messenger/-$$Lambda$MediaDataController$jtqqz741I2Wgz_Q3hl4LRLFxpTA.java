package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$jtqqz741I2Wgz_Q3hl4LRLFxpTA implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$jtqqz741I2Wgz_Q3hl4LRLFxpTA(MediaDataController mediaDataController, Message message) {
        this.f$0 = mediaDataController;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$savePinnedMessage$91$MediaDataController(this.f$1);
    }
}
