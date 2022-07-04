package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateNewMessage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda66 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_updateNewMessage f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda66(SendMessagesHelper sendMessagesHelper, TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_updateNewMessage;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequestMulti$40(this.f$1);
    }
}
