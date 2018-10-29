package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;

final /* synthetic */ class MessagesStorage$$Lambda$41 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final String arg$3;
    private final RequestDelegate arg$4;

    MessagesStorage$$Lambda$41(MessagesStorage messagesStorage, int i, String str, RequestDelegate requestDelegate) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = requestDelegate;
    }

    public void run() {
        this.arg$1.lambda$getBotCache$61$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
