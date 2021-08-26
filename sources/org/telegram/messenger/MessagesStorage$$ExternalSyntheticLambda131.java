package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda131 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda131(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$deleteUserChatHistory$48(this.f$1, this.f$2, this.f$3);
    }
}
