package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$EOPIS8zqBund7flgg_OAgV1TzCA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$EOPIS8zqBund7flgg_OAgV1TzCA(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = arrayList3;
        this.f$4 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$null$27$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
