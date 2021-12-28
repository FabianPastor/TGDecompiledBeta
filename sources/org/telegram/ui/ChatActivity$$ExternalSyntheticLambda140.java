package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda140 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_messages_editMessage f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda140(ChatActivity chatActivity, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_messages_editMessage;
    }

    public final void run() {
        this.f$0.lambda$processSelectedOption$153(this.f$1, this.f$2);
    }
}
