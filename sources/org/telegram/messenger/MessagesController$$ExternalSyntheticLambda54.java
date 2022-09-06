package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getPeerDialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_messages_getPeerDialogs f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda54(MessagesController messagesController, int i, TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_messages_getPeerDialogs;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$loadMessagesInternal$148(this.f$1, this.f$2, this.f$3);
    }
}
