package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda88 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda88(MessagesStorage messagesStorage, long j, long j2, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = arrayList;
        this.f$4 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithDeletedMessages$171(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
