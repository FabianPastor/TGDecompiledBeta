package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.Poll f$2;
    public final /* synthetic */ TLRPC.PollResults f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda1(MessagesStorage messagesStorage, long j, TLRPC.Poll poll, TLRPC.PollResults pollResults) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = poll;
        this.f$3 = pollResults;
    }

    public final void run() {
        this.f$0.m1077x200ecb0f(this.f$1, this.f$2, this.f$3);
    }
}
