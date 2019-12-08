package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$p-5KGhxNlLCRthjDOqOk5m2iUTk implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;

    public /* synthetic */ -$$Lambda$MediaDataController$p-5KGhxNlLCRthjDOqOk5m2iUTk(MediaDataController mediaDataController) {
        this.f$0 = mediaDataController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadDrafts$102$MediaDataController(tLObject, tL_error);
    }
}
