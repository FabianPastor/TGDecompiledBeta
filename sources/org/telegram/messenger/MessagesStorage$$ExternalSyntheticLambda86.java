package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda86 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.messages_Messages f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda86(MessagesStorage messagesStorage, boolean z, long j, TLRPC.messages_Messages messages_messages, int i, int i2, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = j;
        this.f$3 = messages_messages;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = z2;
    }

    public final void run() {
        this.f$0.m1027lambda$putMessages$170$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
