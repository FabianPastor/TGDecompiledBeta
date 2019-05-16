package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$9BkX0ldup_qkhjHCNOhyXfgGwXY implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$9BkX0ldup_qkhjHCNOhyXfgGwXY(MessagesStorage messagesStorage, int i, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$putWallpapers$30$MessagesStorage(this.f$1, this.f$2);
    }
}
