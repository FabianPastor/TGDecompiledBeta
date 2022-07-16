package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_messages_sendScheduledMessages;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda194 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda194(ChatActivity chatActivity, TLRPC$TL_messages_sendScheduledMessages tLRPC$TL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_messages_sendScheduledMessages;
    }

    public final void run() {
        this.f$0.lambda$processSelectedOption$214(this.f$1);
    }
}
