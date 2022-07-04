package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda36 implements RequestDelegate {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda36(MediaController mediaController, int i, int i2) {
        this.f$0 = mediaController;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m92lambda$loadMoreMusic$12$orgtelegrammessengerMediaController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
