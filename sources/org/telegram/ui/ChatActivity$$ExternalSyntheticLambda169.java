package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda169 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda169(ChatActivity chatActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$processSelectedOption$191(this.f$1);
    }
}
