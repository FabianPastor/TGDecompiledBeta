package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$xvVIfZZghazIV9SaEKXBFhK3-7k implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ SparseLongArray f$1;
    private final /* synthetic */ SparseLongArray f$2;
    private final /* synthetic */ SparseIntArray f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ SparseArray f$5;
    private final /* synthetic */ SparseIntArray f$6;

    public /* synthetic */ -$$Lambda$MessagesController$xvVIfZZghazIV9SaEKXBFhK3-7k(MessagesController messagesController, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseIntArray sparseIntArray2) {
        this.f$0 = messagesController;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = sparseIntArray;
        this.f$4 = arrayList;
        this.f$5 = sparseArray;
        this.f$6 = sparseIntArray2;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$256$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}