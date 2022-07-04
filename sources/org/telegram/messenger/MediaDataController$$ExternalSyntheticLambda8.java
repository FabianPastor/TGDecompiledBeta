package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_updateBotCommands f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda8(MediaDataController mediaDataController, TLRPC.TL_updateBotCommands tL_updateBotCommands, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_updateBotCommands;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m2118xf7a6bab2(this.f$1, this.f$2);
    }
}
