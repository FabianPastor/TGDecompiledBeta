package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class DataQuery$$Lambda$57 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final ArrayList arg$4;
    private final Message arg$5;
    private final SparseArray arg$6;
    private final SparseArray arg$7;

    DataQuery$$Lambda$57(DataQuery dataQuery, ArrayList arrayList, boolean z, ArrayList arrayList2, Message message, SparseArray sparseArray, SparseArray sparseArray2) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = arrayList2;
        this.arg$5 = message;
        this.arg$6 = sparseArray;
        this.arg$7 = sparseArray2;
    }

    public void run() {
        this.arg$1.lambda$broadcastPinnedMessage$89$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
