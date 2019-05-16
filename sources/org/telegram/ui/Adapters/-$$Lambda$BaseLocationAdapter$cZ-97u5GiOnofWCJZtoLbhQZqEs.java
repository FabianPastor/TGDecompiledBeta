package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseLocationAdapter$cZ-97u5GiOnofWCJZtoLbhQZqEs implements Runnable {
    private final /* synthetic */ BaseLocationAdapter f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$BaseLocationAdapter$cZ-97u5GiOnofWCJZtoLbhQZqEs(BaseLocationAdapter baseLocationAdapter, TL_error tL_error, TLObject tLObject) {
        this.f$0 = baseLocationAdapter;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$2$BaseLocationAdapter(this.f$1, this.f$2);
    }
}
