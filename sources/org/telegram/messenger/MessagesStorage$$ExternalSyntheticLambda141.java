package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda141 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda141(MessagesStorage messagesStorage, ArrayList arrayList, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$deleteUserChatHistory$58(this.f$1, this.f$2);
    }
}
