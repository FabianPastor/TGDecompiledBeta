package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda50 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.ChatFull f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda50(MessagesController messagesController, TLRPC.ChatFull chatFull, String str) {
        this.f$0 = messagesController;
        this.f$1 = chatFull;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.m416xd34600e5(this.f$1, this.f$2);
    }
}
