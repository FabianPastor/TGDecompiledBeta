package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$FazLRz0lY8TP61ErJo--9_vyvME implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ SparseLongArray f$1;
    private final /* synthetic */ SparseLongArray f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$FazLRz0lY8TP61ErJo--9_vyvME(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = sparseLongArray;
        this.f$2 = sparseLongArray2;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithReadMessages$64$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
