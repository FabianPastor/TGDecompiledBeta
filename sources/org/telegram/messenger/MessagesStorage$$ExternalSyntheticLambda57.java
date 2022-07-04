package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ LongSparseArray f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ CountDownLatch f$8;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda57(MessagesStorage messagesStorage, int i, ArrayList arrayList, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, ArrayList arrayList2, ArrayList arrayList3, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = i2;
        this.f$4 = longSparseArray;
        this.f$5 = longSparseArray2;
        this.f$6 = arrayList2;
        this.f$7 = arrayList3;
        this.f$8 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getWidgetDialogs$133(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
