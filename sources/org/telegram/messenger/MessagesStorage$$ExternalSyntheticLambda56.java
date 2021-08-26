package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ RequestDelegate f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda56(MessagesStorage messagesStorage, int i, String str, RequestDelegate requestDelegate) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = requestDelegate;
    }

    public final void run() {
        this.f$0.lambda$getBotCache$83(this.f$1, this.f$2, this.f$3);
    }
}
