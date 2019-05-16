package org.telegram.messenger;

import java.util.HashMap;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$d0CKfJCWUy1j7KfKMh-A2PiSqHA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$d0CKfJCWUy1j7KfKMh-A2PiSqHA(MessagesStorage messagesStorage, HashMap hashMap, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = hashMap;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$putCachedPhoneBook$89$MessagesStorage(this.f$1, this.f$2);
    }
}
