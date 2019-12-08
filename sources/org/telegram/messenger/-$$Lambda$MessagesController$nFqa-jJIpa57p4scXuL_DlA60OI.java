package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$nFqa-jJIpa57p4scXuL_DlA60OI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$nFqa-jJIpa57p4scXuL_DlA60OI(MessagesController messagesController, ArrayList arrayList, int i) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$270$MessagesController(this.f$1, this.f$2);
    }
}
