package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$Z-XozCjQkTYwiNjREV3PDbzipb4 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MediaDataController$Z-XozCjQkTYwiNjREV3PDbzipb4 INSTANCE = new -$$Lambda$MediaDataController$Z-XozCjQkTYwiNjREV3PDbzipb4();

    private /* synthetic */ -$$Lambda$MediaDataController$Z-XozCjQkTYwiNjREV3PDbzipb4() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$27(tLObject, tL_error);
    }
}
