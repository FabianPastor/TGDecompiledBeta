package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda81 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.Message f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda81(MediaDataController mediaDataController, long j, TLRPC.Message message) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = message;
    }

    public final void run() {
        this.f$0.m872x946411c7(this.f$1, this.f$2);
    }
}
