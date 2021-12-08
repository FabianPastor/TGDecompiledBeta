package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda74 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.Message f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda74(MediaDataController mediaDataController, long j, int i, TLRPC.Message message) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = message;
    }

    public final void run() {
        this.f$0.m887x7522719a(this.f$1, this.f$2, this.f$3);
    }
}
