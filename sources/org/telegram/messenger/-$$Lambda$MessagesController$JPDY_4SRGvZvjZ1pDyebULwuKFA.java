package org.telegram.messenger;

import android.util.SparseIntArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$JPDY_4SRGvZvjZ1pDyebULwuKFA implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ SparseIntArray f$3;

    public /* synthetic */ -$$Lambda$MessagesController$JPDY_4SRGvZvjZ1pDyebULwuKFA(MessagesController messagesController, ArrayList arrayList, boolean z, SparseIntArray sparseIntArray) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = sparseIntArray;
    }

    public final void run() {
        this.f$0.lambda$processLoadedBlockedUsers$55$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
