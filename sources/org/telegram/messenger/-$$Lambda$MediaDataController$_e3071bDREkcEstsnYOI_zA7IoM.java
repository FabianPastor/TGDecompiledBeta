package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$_e3071bDREkcEstsnYOI_zA7IoM implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$_e3071bDREkcEstsnYOI_zA7IoM(MediaDataController mediaDataController, int i, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadStickers$38$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
