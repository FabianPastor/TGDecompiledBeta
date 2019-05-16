package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_Difference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$CTc5p-O22dGYncyM4gxumAZNVws implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ updates_Difference f$2;

    public /* synthetic */ -$$Lambda$MessagesController$CTc5p-O22dGYncyM4gxumAZNVws(MessagesController messagesController, ArrayList arrayList, updates_Difference updates_difference) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = updates_difference;
    }

    public final void run() {
        this.f$0.lambda$null$211$MessagesController(this.f$1, this.f$2);
    }
}
