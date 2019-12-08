package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.BotInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$DUALcSm-gJlVK8mWhEl3j7-JXbw implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ BotInfo f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$DUALcSm-gJlVK8mWhEl3j7-JXbw(MediaDataController mediaDataController, BotInfo botInfo) {
        this.f$0 = mediaDataController;
        this.f$1 = botInfo;
    }

    public final void run() {
        this.f$0.lambda$putBotInfo$112$MediaDataController(this.f$1);
    }
}
