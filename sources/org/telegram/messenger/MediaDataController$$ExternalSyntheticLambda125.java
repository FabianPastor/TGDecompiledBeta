package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda125 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda125(MediaDataController mediaDataController, TLRPC.Message message, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = message;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m805xe60287fc(this.f$1, this.f$2);
    }
}
