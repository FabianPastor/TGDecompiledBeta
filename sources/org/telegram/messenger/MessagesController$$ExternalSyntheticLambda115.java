package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda115 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.updates_Difference f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda115(MessagesController messagesController, TLRPC.updates_Difference updates_difference, ArrayList arrayList, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = updates_difference;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
        this.f$4 = longSparseArray2;
    }

    public final void run() {
        this.f$0.m229x4ee84f3f(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
