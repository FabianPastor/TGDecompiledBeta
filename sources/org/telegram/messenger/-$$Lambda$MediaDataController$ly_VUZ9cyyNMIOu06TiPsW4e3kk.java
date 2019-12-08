package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ly_VUZ9cyyNMIOu06TiPsW4e3kk implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$ly_VUZ9cyyNMIOu06TiPsW4e3kk(MediaDataController mediaDataController, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadPinnedMessageInternal$90$MediaDataController(this.f$1, tLObject, tL_error);
    }
}
