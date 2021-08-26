package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda179 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_Difference f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ SparseArray f$3;
    public final /* synthetic */ SparseArray f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda179(MessagesController messagesController, TLRPC$updates_Difference tLRPC$updates_Difference, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_Difference;
        this.f$2 = arrayList;
        this.f$3 = sparseArray;
        this.f$4 = sparseArray2;
    }

    public final void run() {
        this.f$0.lambda$getDifference$261(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
