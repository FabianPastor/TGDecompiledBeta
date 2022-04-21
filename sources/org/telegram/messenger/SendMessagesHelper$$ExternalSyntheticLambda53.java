package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda53 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_messages_forwardMessages f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda53(SendMessagesHelper sendMessagesHelper, TLRPC.TL_error tL_error, TLRPC.TL_messages_forwardMessages tL_messages_forwardMessages) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = tL_messages_forwardMessages;
    }

    public final void run() {
        this.f$0.m480lambda$sendMessage$12$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2);
    }
}
