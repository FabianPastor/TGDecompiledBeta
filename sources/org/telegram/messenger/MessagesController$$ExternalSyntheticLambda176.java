package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_messages_sponsoredMessages;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda176 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_messages_sponsoredMessages f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda176(MessagesController messagesController, TLRPC$TL_messages_sponsoredMessages tLRPC$TL_messages_sponsoredMessages) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_messages_sponsoredMessages;
    }

    public final void run() {
        this.f$0.lambda$getSponsoredMessages$338(this.f$1);
    }
}
