package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda128 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda128(MessagesStorage messagesStorage, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$putMessages$172(this.f$1);
    }
}
