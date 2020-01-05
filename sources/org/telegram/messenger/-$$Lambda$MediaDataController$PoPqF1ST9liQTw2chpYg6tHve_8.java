package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.BotInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$PoPqF1ST9liQTw2chpYg6tHve_8 implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ BotInfo f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$PoPqF1ST9liQTw2chpYg6tHve_8(MediaDataController mediaDataController, BotInfo botInfo) {
        this.f$0 = mediaDataController;
        this.f$1 = botInfo;
    }

    public final void run() {
        this.f$0.lambda$putBotInfo$113$MediaDataController(this.f$1);
    }
}
