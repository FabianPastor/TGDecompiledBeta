package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$HcaX9dRNfoCWz9JypBQKVvGyzlc implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$HcaX9dRNfoCWz9JypBQKVvGyzlc(MessagesStorage messagesStorage, boolean z, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$putContacts$84$MessagesStorage(this.f$1, this.f$2);
    }
}
