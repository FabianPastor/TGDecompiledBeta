package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$9mqXwvzFXcBLtC8iNVGWj-JJop8 implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$9mqXwvzFXcBLtC8iNVGWj-JJop8(MediaDataController mediaDataController, int[] iArr, int i, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$57$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
