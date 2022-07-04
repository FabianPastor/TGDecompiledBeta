package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda27 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.updates_Difference f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda27(MessagesController messagesController, ArrayList arrayList, TLRPC.updates_Difference updates_difference, LongSparseArray longSparseArray) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = updates_difference;
        this.f$3 = longSparseArray;
    }

    public final void run() {
        this.f$0.m227x846571bd(this.f$1, this.f$2, this.f$3);
    }
}
