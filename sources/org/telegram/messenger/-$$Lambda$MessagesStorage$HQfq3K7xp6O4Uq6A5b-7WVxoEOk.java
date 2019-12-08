package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$HQfq3K7xp6O4Uq6A5b-7WVxoEOk implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$HQfq3K7xp6O4Uq6A5b-7WVxoEOk(MessagesStorage messagesStorage, ArrayList arrayList, int i, boolean z, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = z2;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsDeleted$134$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
