package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda142 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ SparseLongArray f$1;
    public final /* synthetic */ SparseLongArray f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda142(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithReadMessages$76(this.f$1, this.f$2, this.f$3);
    }
}
