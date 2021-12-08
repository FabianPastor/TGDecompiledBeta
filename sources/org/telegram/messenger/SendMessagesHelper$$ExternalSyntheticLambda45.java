package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda45 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda45(SendMessagesHelper sendMessagesHelper, TLRPC$Message tLRPC$Message, int i) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Message;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$61(this.f$1, this.f$2);
    }
}
