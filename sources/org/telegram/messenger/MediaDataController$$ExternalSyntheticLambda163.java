package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda163 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda163(MediaDataController mediaDataController, TLRPC.Message message, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = message;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m2006xa11cdfbc(this.f$1, this.f$2);
    }
}
