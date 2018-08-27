package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$70 implements Runnable {
    private final MessagesStorage arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final boolean arg$4;

    MessagesStorage$$Lambda$70(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$putUsersAndChats$94$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
