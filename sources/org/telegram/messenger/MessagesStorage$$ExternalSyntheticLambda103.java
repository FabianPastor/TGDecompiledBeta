package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda103 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ SparseArray f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ SparseArray f$3;
    public final /* synthetic */ SparseArray f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda103(MessagesStorage messagesStorage, SparseArray sparseArray, boolean z, SparseArray sparseArray2, SparseArray sparseArray3, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseArray;
        this.f$2 = z;
        this.f$3 = sparseArray2;
        this.f$4 = sparseArray3;
        this.f$5 = z2;
    }

    public final void run() {
        this.f$0.lambda$putChannelViews$141(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
