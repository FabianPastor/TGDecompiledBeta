package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_sendScheduledMessages;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda158 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_messages_sendScheduledMessages f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda158(ChatActivity chatActivity, TLRPC$TL_messages_sendScheduledMessages tLRPC$TL_messages_sendScheduledMessages) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_messages_sendScheduledMessages;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$processSelectedOption$145(this.f$1, tLObject, tLRPC$TL_error);
    }
}
