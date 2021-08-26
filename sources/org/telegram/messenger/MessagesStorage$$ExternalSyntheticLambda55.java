package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ SparseArray f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda55(MessagesStorage messagesStorage, int i, SparseArray sparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$putChannelAdmins$80(this.f$1, this.f$2);
    }
}
