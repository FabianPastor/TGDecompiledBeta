package org.telegram.messenger;

import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda126 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ LongSparseIntArray f$1;
    public final /* synthetic */ LongSparseIntArray f$2;
    public final /* synthetic */ SparseIntArray f$3;
    public final /* synthetic */ LongSparseArray f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ LongSparseArray f$6;
    public final /* synthetic */ LongSparseIntArray f$7;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda126(MessagesController messagesController, LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, SparseIntArray sparseIntArray, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, LongSparseIntArray longSparseIntArray3) {
        this.f$0 = messagesController;
        this.f$1 = longSparseIntArray;
        this.f$2 = longSparseIntArray2;
        this.f$3 = sparseIntArray;
        this.f$4 = longSparseArray;
        this.f$5 = longSparseArray2;
        this.f$6 = longSparseArray3;
        this.f$7 = longSparseIntArray3;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$318(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
