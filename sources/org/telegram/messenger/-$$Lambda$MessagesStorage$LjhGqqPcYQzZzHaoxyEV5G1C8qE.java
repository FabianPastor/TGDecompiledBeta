package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$LjhGqqPcYQzZzHaoxyEV5G1C8qE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$LjhGqqPcYQzZzHaoxyEV5G1C8qE(MessagesStorage messagesStorage, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$markMessagesAsDeletedByRandoms$131$MessagesStorage(this.f$1);
    }
}
