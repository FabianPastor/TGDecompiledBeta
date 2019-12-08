package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_Difference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Go9_xyx9UJXYHkm0ms9PsBii500 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ updates_Difference f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ SparseArray f$3;
    private final /* synthetic */ SparseArray f$4;

    public /* synthetic */ -$$Lambda$MessagesController$Go9_xyx9UJXYHkm0ms9PsBii500(MessagesController messagesController, updates_Difference updates_difference, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = updates_difference;
        this.f$2 = arrayList;
        this.f$3 = sparseArray;
        this.f$4 = sparseArray2;
    }

    public final void run() {
        this.f$0.lambda$null$217$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
