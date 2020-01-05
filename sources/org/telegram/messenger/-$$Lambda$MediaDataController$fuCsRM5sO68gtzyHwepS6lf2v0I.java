package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$fuCsRM5sO68gtzyHwepS6lf2v0I implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$fuCsRM5sO68gtzyHwepS6lf2v0I(MediaDataController mediaDataController, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$103$MediaDataController(this.f$1, tLObject, tL_error);
    }
}
