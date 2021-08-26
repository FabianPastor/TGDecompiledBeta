package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda178 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_Difference f$1;
    public final /* synthetic */ SparseArray f$2;
    public final /* synthetic */ SparseArray f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda178(MessagesController messagesController, TLRPC$updates_Difference tLRPC$updates_Difference, SparseArray sparseArray, SparseArray sparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_Difference;
        this.f$2 = sparseArray;
        this.f$3 = sparseArray2;
    }

    public final void run() {
        this.f$0.lambda$getDifference$260(this.f$1, this.f$2, this.f$3);
    }
}
