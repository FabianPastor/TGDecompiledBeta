package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda32(ChatActivity chatActivity, TLRPC.TL_error tL_error) {
        this.f$0 = chatActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m1780lambda$processSelectedOption$144$orgtelegramuiChatActivity(this.f$1);
    }
}
