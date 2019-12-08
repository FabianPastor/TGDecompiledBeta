package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$C3bCDyUmT7WiWn18OC_3XoGM8Ak implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ SparseArray f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$C3bCDyUmT7WiWn18OC_3XoGM8Ak(MediaDataController mediaDataController, SparseArray sparseArray, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = sparseArray;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$94$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
