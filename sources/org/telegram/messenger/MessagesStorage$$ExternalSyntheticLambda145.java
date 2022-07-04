package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda145 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.Message f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda145(MessagesStorage messagesStorage, int i, long j, TLRPC.Message message, String str) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = message;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.m2307xcf8e7f2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
