package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Fe6gCKpZXcvUQ22h30h7qRuTYu8 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ SparseLongArray f$1;
    private final /* synthetic */ SparseLongArray f$2;
    private final /* synthetic */ SparseIntArray f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ SparseArray f$5;
    private final /* synthetic */ SparseArray f$6;
    private final /* synthetic */ SparseIntArray f$7;

    public /* synthetic */ -$$Lambda$MessagesController$Fe6gCKpZXcvUQ22h30h7qRuTYu8(MessagesController messagesController, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2, SparseIntArray sparseIntArray2) {
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
        this.f$0.lambda$processUpdateArray$269$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
