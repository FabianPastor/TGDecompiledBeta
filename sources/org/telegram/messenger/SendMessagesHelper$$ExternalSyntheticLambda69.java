package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda69(SendMessagesHelper sendMessagesHelper, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$54(this.f$1);
    }
}
