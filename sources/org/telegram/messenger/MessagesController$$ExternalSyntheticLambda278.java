package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda278 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.messages_Messages f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda278(MessagesController messagesController, int i, TLRPC.messages_Messages messages_messages, boolean z, boolean z2, int i2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = messages_messages;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.m316xa7218152(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
