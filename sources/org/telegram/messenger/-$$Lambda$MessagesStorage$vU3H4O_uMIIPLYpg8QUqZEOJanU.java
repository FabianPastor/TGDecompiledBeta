package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$vU3H4O_uMIIPLYpg8QUqZEOJanU implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ long f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$vU3H4O_uMIIPLYpg8QUqZEOJanU(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = i;
        this.f$4 = j;
    }

    public final void run() {
        this.f$0.lambda$setDialogsFolderId$142$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
