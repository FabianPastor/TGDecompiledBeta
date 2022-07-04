package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda14(MediaController mediaController, int i, TLRPC.TL_error tL_error, TLObject tLObject, int i2) {
        this.f$0 = mediaController;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.m91lambda$loadMoreMusic$11$orgtelegrammessengerMediaController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
