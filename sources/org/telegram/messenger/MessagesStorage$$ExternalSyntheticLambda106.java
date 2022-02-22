package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$PollResults;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda106 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$Poll f$2;
    public final /* synthetic */ TLRPC$PollResults f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda106(MessagesStorage messagesStorage, long j, TLRPC$Poll tLRPC$Poll, TLRPC$PollResults tLRPC$PollResults) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = tLRPC$Poll;
        this.f$3 = tLRPC$PollResults;
    }

    public final void run() {
        this.f$0.lambda$updateMessagePollResults$79(this.f$1, this.f$2, this.f$3);
    }
}
