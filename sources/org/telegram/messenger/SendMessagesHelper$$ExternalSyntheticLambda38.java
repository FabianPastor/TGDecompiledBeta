package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda38 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ TLRPC$Message f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda38(SendMessagesHelper sendMessagesHelper, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = messageObject;
        this.f$2 = tLRPC$Message;
        this.f$3 = i;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$55(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
