package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$-kJL0n7IgW6dWhzxhVs7OvY_IpY implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;

    public /* synthetic */ -$$Lambda$MediaDataController$-kJL0n7IgW6dWhzxhVs7OvY_IpY(MediaDataController mediaDataController) {
        this.f$0 = mediaDataController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadHints$75$MediaDataController(tLObject, tL_error);
    }
}
