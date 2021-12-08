package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda126 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda126(MediaDataController mediaDataController, TLRPC.Message message, String str) {
        this.f$0 = mediaDataController;
        this.f$1 = message;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.m904x60ca8350(this.f$1, this.f$2);
    }
}
