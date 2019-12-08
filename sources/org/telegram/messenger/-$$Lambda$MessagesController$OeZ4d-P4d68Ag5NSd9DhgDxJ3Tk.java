package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$OeZ4d-P4d68Ag5NSd9DhgDxJ3Tk implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$MessagesController$OeZ4d-P4d68Ag5NSd9DhgDxJ3Tk(MessagesController messagesController, boolean z, int i, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processUpdates$238$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
