package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$LXNvesRV73M8Th_uo8wE7kp1kKo implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ SparseArray f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ Runnable f$4;

    public /* synthetic */ -$$Lambda$MediaDataController$LXNvesRV73M8Th_uo8wE7kp1kKo(MediaDataController mediaDataController, SparseArray sparseArray, long j, boolean z, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = sparseArray;
        this.f$2 = j;
        this.f$3 = z;
        this.f$4 = runnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$95$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
