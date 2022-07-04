package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateShortSentMessage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda67 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_updateShortSentMessage f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda67(SendMessagesHelper sendMessagesHelper, TLRPC$TL_updateShortSentMessage tLRPC$TL_updateShortSentMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_updateShortSentMessage;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$51(this.f$1);
    }
}
