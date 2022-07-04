package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda170 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_attachMenuBots f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda170(MediaDataController mediaDataController, TLRPC.TL_attachMenuBots tL_attachMenuBots, long j, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_attachMenuBots;
        this.f$2 = j;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m2089x7981acd6(this.f$1, this.f$2, this.f$3);
    }
}
