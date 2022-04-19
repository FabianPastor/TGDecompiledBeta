package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda119 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$updates_Difference f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda119(MessagesController messagesController, ArrayList arrayList, TLRPC$updates_Difference tLRPC$updates_Difference, LongSparseArray longSparseArray) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$updates_Difference;
        this.f$3 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$getDifference$272(this.f$1, this.f$2, this.f$3);
    }
}
