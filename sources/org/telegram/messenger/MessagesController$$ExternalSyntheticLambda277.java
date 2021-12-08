package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda277 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_messages_getReplies f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda277(MessagesController messagesController, int i, TLRPC.TL_messages_getReplies tL_messages_getReplies, TLRPC.TL_error tL_error) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_messages_getReplies;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m252xbbCLASSNAMEc(this.f$1, this.f$2, this.f$3);
    }
}
