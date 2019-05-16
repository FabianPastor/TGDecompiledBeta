package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$DP0ftrGTBszIUd99KwPuGmnwqBM implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ SparseArray f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$DataQuery$DP0ftrGTBszIUd99KwPuGmnwqBM(DataQuery dataQuery, SparseArray sparseArray, long j) {
        this.f$0 = dataQuery;
        this.f$1 = sparseArray;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$93$DataQuery(this.f$1, this.f$2, tLObject, tL_error);
    }
}
