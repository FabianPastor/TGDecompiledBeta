package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$nKs7i4EVICboKTM7F5hPQHDG0WE implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesController$nKs7i4EVICboKTM7F5hPQHDG0WE(MessagesController messagesController, ArrayList arrayList, int i, long j) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$deleteMessagesByPush$242$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}