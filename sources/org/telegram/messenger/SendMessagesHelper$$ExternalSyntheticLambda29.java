package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda29 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ TLRPC.Message f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda29(SendMessagesHelper sendMessagesHelper, MessageObject messageObject, TLRPC.Message message, int i, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = messageObject;
        this.f$2 = message;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.m483lambda$sendMessage$7$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
