package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$Yx2wTX87h9OMWDNW8TJbWVjbdlk implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ Message f$4;
    private final /* synthetic */ SparseArray f$5;
    private final /* synthetic */ SparseArray f$6;

    public /* synthetic */ -$$Lambda$DataQuery$Yx2wTX87h9OMWDNW8TJbWVjbdlk(DataQuery dataQuery, ArrayList arrayList, boolean z, ArrayList arrayList2, Message message, SparseArray sparseArray, SparseArray sparseArray2) {
        this.f$0 = dataQuery;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = arrayList2;
        this.f$4 = message;
        this.f$5 = sparseArray;
        this.f$6 = sparseArray2;
    }

    public final void run() {
        this.f$0.lambda$broadcastPinnedMessage$89$DataQuery(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
