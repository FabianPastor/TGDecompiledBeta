package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda22(MediaController mediaController, int i, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i2) {
        this.f$0 = mediaController;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLObject;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$loadMoreMusic$11(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
