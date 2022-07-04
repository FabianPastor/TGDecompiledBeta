package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$Message f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda48(MessagesStorage messagesStorage, int i, long j, TLRPC$Message tLRPC$Message) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = tLRPC$Message;
    }

    public final void run() {
        this.f$0.lambda$updateMessageVoiceTranscriptionOpen$76(this.f$1, this.f$2, this.f$3);
    }
}
