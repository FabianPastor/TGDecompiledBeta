package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda186 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda186(MessagesStorage messagesStorage, boolean z, long j, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = j;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$createTaskForMid$83(this.f$1, this.f$2, this.f$3);
    }
}
