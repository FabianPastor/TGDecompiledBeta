package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda95 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$TL_updates_channelDifferenceTooLong f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda95(MessagesStorage messagesStorage, long j, TLRPC$TL_updates_channelDifferenceTooLong tLRPC$TL_updates_channelDifferenceTooLong) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = tLRPC$TL_updates_channelDifferenceTooLong;
    }

    public final void run() {
        this.f$0.lambda$overwriteChannel$139(this.f$1, this.f$2);
    }
}
