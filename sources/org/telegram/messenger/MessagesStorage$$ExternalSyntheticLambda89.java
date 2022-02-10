package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda89 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda89(MessagesStorage messagesStorage, long j, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$createTaskForSecretChat$81(this.f$1, this.f$2);
    }
}
