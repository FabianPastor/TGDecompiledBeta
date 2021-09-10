package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda134 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda134(MessagesStorage messagesStorage, ArrayList arrayList, long j, ArrayList arrayList2) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$deleteUserChatHistory$53(this.f$1, this.f$2, this.f$3);
    }
}
