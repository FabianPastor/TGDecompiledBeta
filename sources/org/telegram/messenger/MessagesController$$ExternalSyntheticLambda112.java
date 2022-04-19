package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda112 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda112(MessagesController messagesController, ArrayList arrayList, long j, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$deleteMessagesRange$345(this.f$1, this.f$2, this.f$3);
    }
}
