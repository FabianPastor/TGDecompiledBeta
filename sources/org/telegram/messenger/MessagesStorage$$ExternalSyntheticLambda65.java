package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda65 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.Dialog f$1;
    public final /* synthetic */ TLRPC.InputPeer f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda65(MessagesStorage messagesStorage, TLRPC.Dialog dialog, TLRPC.InputPeer inputPeer, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = dialog;
        this.f$2 = inputPeer;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.m917x4b6caa8a(this.f$1, this.f$2, this.f$3);
    }
}
