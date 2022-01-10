package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_messages_sponsoredMessages;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda161 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_messages_sponsoredMessages f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda161(MessagesController messagesController, TLRPC$TL_messages_sponsoredMessages tLRPC$TL_messages_sponsoredMessages) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_messages_sponsoredMessages;
    }

    public final void run() {
        this.f$0.lambda$getSponsoredMessages$316(this.f$1);
    }
}
