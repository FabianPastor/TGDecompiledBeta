package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ZxzfDpi-FX2c6BmdS9IWQvzycIc implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ SparseArray f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$ZxzfDpi-FX2c6BmdS9IWQvzycIc(MediaDataController mediaDataController, SparseArray sparseArray, long j, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = sparseArray;
        this.f$2 = j;
        this.f$3 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$95$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
