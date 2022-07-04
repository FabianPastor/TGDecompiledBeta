package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda57(MessagesController messagesController, TLRPC.Chat chat, boolean z, int i) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = z;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m410x409983(this.f$1, this.f$2, this.f$3);
    }
}
