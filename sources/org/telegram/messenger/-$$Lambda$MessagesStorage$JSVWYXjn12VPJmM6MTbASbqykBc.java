package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$JSVWYXjn12VPJmM6MTbASbqykBc implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$JSVWYXjn12VPJmM6MTbASbqykBc(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$putUsersAndChats$112$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}