package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateNewMessage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda65 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_updateNewMessage f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda65(SendMessagesHelper sendMessagesHelper, TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_updateNewMessage;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$52(this.f$1);
    }
}
