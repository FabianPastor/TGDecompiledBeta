package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda187 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_Difference f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda187(MessagesController messagesController, TLRPC$updates_Difference tLRPC$updates_Difference, ArrayList arrayList, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_Difference;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
        this.f$4 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$getDifference$265(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
