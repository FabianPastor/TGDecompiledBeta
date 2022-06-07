package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda53 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda53(SendMessagesHelper sendMessagesHelper, TLRPC$Message tLRPC$Message, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Message;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$48(this.f$1, this.f$2);
    }
}
