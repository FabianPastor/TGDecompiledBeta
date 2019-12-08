package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC.updates_Difference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$8kEJe4WEA965CY8-zzNnmFWHYUM implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ updates_Difference f$1;
    private final /* synthetic */ SparseArray f$2;
    private final /* synthetic */ SparseArray f$3;

    public /* synthetic */ -$$Lambda$MessagesController$8kEJe4WEA965CY8-zzNnmFWHYUM(MessagesController messagesController, updates_Difference updates_difference, SparseArray sparseArray, SparseArray sparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = updates_difference;
        this.f$2 = sparseArray;
        this.f$3 = sparseArray2;
    }

    public final void run() {
        this.f$0.lambda$null$227$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
