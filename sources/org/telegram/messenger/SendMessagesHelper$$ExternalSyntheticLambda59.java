package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda59 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_messages_forwardMessages f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda59(SendMessagesHelper sendMessagesHelper, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_forwardMessages tLRPC$TL_messages_forwardMessages) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_messages_forwardMessages;
    }

    public final void run() {
        this.f$0.lambda$sendMessage$12(this.f$1, this.f$2);
    }
}
