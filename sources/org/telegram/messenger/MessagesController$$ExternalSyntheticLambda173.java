package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_help_termsOfServiceUpdate;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda173 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_help_termsOfServiceUpdate f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda173(MessagesController messagesController, TLRPC$TL_help_termsOfServiceUpdate tLRPC$TL_help_termsOfServiceUpdate) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_help_termsOfServiceUpdate;
    }

    public final void run() {
        this.f$0.lambda$checkTosUpdate$132(this.f$1);
    }
}
