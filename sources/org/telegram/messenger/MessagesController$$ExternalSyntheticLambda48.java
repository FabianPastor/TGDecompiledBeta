package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda48(MessagesController messagesController, int i, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$generateJoinMessage$273(this.f$1, this.f$2);
    }
}
