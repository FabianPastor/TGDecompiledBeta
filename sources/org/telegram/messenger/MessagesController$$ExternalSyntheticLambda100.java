package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda100 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda100(MessagesController messagesController, ArrayList arrayList, int i, long j) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$deleteMessagesByPush$275(this.f$1, this.f$2, this.f$3);
    }
}
