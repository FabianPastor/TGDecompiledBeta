package org.telegram.messenger;

import java.util.HashMap;

final /* synthetic */ class MessagesStorage$$Lambda$51 implements Runnable {
    private final MessagesStorage arg$1;
    private final HashMap arg$2;
    private final boolean arg$3;

    MessagesStorage$$Lambda$51(MessagesStorage messagesStorage, HashMap hashMap, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = hashMap;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$putCachedPhoneBook$73$MessagesStorage(this.arg$2, this.arg$3);
    }
}
