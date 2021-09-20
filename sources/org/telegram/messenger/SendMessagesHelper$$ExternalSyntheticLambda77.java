package org.telegram.messenger;

import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda77 implements QuickAckDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda77(SendMessagesHelper sendMessagesHelper, TLRPC$Message tLRPC$Message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Message;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$62(this.f$1);
    }
}
