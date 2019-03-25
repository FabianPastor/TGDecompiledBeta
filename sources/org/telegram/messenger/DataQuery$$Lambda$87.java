package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$87 implements RequestDelegate {
    private final DataQuery arg$1;
    private final SparseArray arg$2;
    private final long arg$3;

    DataQuery$$Lambda$87(DataQuery dataQuery, SparseArray sparseArray, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = sparseArray;
        this.arg$3 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$92$DataQuery(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
