package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Updates f$1;
    public final /* synthetic */ TLRPC$Message f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda69(SendMessagesHelper sendMessagesHelper, TLRPC$Updates tLRPC$Updates, TLRPC$Message tLRPC$Message, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Updates;
        this.f$2 = tLRPC$Message;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$49(this.f$1, this.f$2, this.f$3);
    }
}
