package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$stqvu1S1fXbEKuIXuRf_-ubdb58 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$stqvu1S1fXbEKuIXuRf_-ubdb58(MessagesController messagesController, ArrayList arrayList, int i) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDeleteTask$44$MessagesController(this.f$1, this.f$2);
    }
}
