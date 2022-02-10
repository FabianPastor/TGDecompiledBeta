package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$ChatParticipants;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda155 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$ChatParticipants f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda155(MessagesStorage messagesStorage, TLRPC$ChatParticipants tLRPC$ChatParticipants) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$ChatParticipants;
    }

    public final void run() {
        this.f$0.lambda$updateChatParticipants$86(this.f$1);
    }
}
