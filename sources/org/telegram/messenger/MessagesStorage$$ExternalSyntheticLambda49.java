package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$Message f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda49(MessagesStorage messagesStorage, int i, long j, TLRPC$Message tLRPC$Message, String str) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = tLRPC$Message;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.lambda$updateMessageVoiceTranscription$78(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
