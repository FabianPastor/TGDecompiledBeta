package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$U1zBGSD7nENssAbAXgrCmtYpcyg implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$U1zBGSD7nENssAbAXgrCmtYpcyg(MessagesStorage messagesStorage, Message message) {
        this.f$0 = messagesStorage;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$markMessageAsSendError$122$MessagesStorage(this.f$1);
    }
}
