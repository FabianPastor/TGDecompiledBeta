package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda113 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda113(MessagesController messagesController, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processUpdates$305(this.f$1);
    }
}
