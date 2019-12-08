package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$7jxazQcOI7oIf1EeZlB3YOh2uLo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ RequestDelegate f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$7jxazQcOI7oIf1EeZlB3YOh2uLo(MessagesStorage messagesStorage, int i, String str, RequestDelegate requestDelegate) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = requestDelegate;
    }

    public final void run() {
        this.f$0.lambda$getBotCache$71$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
