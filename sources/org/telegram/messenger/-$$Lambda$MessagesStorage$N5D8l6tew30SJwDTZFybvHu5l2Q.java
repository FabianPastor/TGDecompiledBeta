package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$N5D8l6tew30SJwDTZFybvHu5l2Q implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ RequestDelegate f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$N5D8l6tew30SJwDTZFybvHu5l2Q(MessagesStorage messagesStorage, int i, String str, RequestDelegate requestDelegate) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = requestDelegate;
    }

    public final void run() {
        this.f$0.lambda$getBotCache$70$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}