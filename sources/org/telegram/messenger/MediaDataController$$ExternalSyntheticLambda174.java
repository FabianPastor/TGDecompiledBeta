package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda174 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda174(MediaDataController mediaDataController, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m2000x66b209a3(this.f$1, this.f$2, this.f$3);
    }
}
