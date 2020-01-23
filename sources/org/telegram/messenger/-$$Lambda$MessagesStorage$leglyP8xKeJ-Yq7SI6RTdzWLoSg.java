package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.PollResults;
import org.telegram.tgnet.TLRPC.TL_poll;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$leglyP8xKeJ-Yq7SI6RTdzWLoSg implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ TL_poll f$2;
    private final /* synthetic */ PollResults f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$leglyP8xKeJ-Yq7SI6RTdzWLoSg(MessagesStorage messagesStorage, long j, TL_poll tL_poll, PollResults pollResults) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = tL_poll;
        this.f$3 = pollResults;
    }

    public final void run() {
        this.f$0.lambda$updateMessagePollResults$50$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
