package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda122 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.BotInfo f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda122(MediaDataController mediaDataController, TLRPC.BotInfo botInfo, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = botInfo;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m871lambda$putBotInfo$135$orgtelegrammessengerMediaDataController(this.f$1, this.f$2);
    }
}
