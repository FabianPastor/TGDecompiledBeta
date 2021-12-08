package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda64 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda64(ChatActivity chatActivity, TLRPC.TL_messages_sendScheduledMessages tL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_sendScheduledMessages;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1781lambda$processSelectedOption$145$orgtelegramuiChatActivity(this.f$1, tLObject, tL_error);
    }
}
