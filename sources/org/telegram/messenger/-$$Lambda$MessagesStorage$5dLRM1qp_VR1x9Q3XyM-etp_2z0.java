package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$5dLRM1qp_VR1x9Q3XyM-etp_2z0 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ SparseLongArray f$1;
    private final /* synthetic */ SparseLongArray f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$5dLRM1qp_VR1x9Q3XyM-etp_2z0(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithReadMessages$60$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
