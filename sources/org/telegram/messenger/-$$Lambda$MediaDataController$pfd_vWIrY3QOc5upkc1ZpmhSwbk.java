package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$pfd_vWIrY3QOc5upkc1ZpmhSwbk implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;

    public /* synthetic */ -$$Lambda$MediaDataController$pfd_vWIrY3QOc5upkc1ZpmhSwbk(MediaDataController mediaDataController) {
        this.f$0 = mediaDataController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadHints$77$MediaDataController(tLObject, tL_error);
    }
}
