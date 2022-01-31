package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda116 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ ArrayList f$6;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda116(MessagesStorage messagesStorage, LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
        this.f$5 = arrayList4;
        this.f$6 = arrayList5;
    }

    public final void run() {
        this.f$0.lambda$loadUnreadMessages$48(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
