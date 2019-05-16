package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$56oQ4aHz7XTwkr1eroOd47iXRqM implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$56oQ4aHz7XTwkr1eroOd47iXRqM(MessagesStorage messagesStorage, ArrayList arrayList, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$unpinAllDialogsExceptNew$143$MessagesStorage(this.f$1, this.f$2);
    }
}
