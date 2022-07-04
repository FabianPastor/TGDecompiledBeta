package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda103 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda103(ChatActivity chatActivity, TLRPC.TL_messages_sendScheduledMessages tL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_sendScheduledMessages;
    }

    public final void run() {
        this.f$0.m3076lambda$processSelectedOption$213$orgtelegramuiChatActivity(this.f$1);
    }
}
