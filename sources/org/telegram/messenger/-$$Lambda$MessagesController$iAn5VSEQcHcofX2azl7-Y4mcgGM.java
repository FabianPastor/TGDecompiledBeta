package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$iAn5VSEQcHcofX2azl7-Y4mcgGM implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$MessagesController$iAn5VSEQcHcofX2azl7-Y4mcgGM(MessagesController messagesController, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processUpdates$235$MessagesController(this.f$1);
    }
}
