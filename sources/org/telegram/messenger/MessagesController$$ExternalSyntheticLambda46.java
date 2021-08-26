package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda46 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ SparseArray f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda46(MessagesController messagesController, int i, SparseArray sparseArray, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = sparseArray;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedChannelAdmins$38(this.f$1, this.f$2, this.f$3);
    }
}
