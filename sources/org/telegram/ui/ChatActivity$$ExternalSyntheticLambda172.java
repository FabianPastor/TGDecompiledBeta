package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_messages_sendScheduledMessages;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda172 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda172(ChatActivity chatActivity, TLRPC$TL_messages_sendScheduledMessages tLRPC$TL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_messages_sendScheduledMessages;
    }

    public final void run() {
        this.f$0.lambda$processSelectedOption$190(this.f$1);
    }
}
