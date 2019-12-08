package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_Difference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$bREMH_Fm8aJlnszx3lWybcIEEb4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ updates_Difference f$2;

    public /* synthetic */ -$$Lambda$MessagesController$bREMH_Fm8aJlnszx3lWybcIEEb4(MessagesController messagesController, ArrayList arrayList, updates_Difference updates_difference) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = updates_difference;
    }

    public final void run() {
        this.f$0.lambda$null$227$MessagesController(this.f$1, this.f$2);
    }
}
