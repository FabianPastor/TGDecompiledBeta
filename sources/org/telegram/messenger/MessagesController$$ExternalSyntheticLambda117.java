package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda117 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ SparseLongArray f$1;
    public final /* synthetic */ SparseLongArray f$2;
    public final /* synthetic */ SparseIntArray f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ SparseArray f$5;
    public final /* synthetic */ SparseArray f$6;
    public final /* synthetic */ SparseIntArray f$7;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda117(MessagesController messagesController, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2, SparseIntArray sparseIntArray2) {
        this.f$0 = messagesController;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = sparseIntArray;
        this.f$4 = arrayList;
        this.f$5 = sparseArray;
        this.f$6 = sparseArray2;
        this.f$7 = sparseIntArray2;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$305(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
