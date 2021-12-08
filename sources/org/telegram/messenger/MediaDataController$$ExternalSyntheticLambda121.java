package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda121 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.BotInfo f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda121(MediaDataController mediaDataController, TLRPC.BotInfo botInfo, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = botInfo;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m803xfba9bd13(this.f$1, this.f$2);
    }
}
