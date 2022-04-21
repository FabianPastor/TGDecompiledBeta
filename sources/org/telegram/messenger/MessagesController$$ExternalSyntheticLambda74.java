package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda74 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_help_termsOfServiceUpdate f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda74(MessagesController messagesController, TLRPC.TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate) {
        this.f$0 = messagesController;
        this.f$1 = tL_help_termsOfServiceUpdate;
    }

    public final void run() {
        this.f$0.m147xvar_b(this.f$1);
    }
}
