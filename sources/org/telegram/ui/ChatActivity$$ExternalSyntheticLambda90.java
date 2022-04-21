package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda90 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_messages_editMessage f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda90(ChatActivity chatActivity, TLRPC.TL_error tL_error, TLRPC.TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = chatActivity;
        this.f$1 = tL_error;
        this.f$2 = tL_messages_editMessage;
    }

    public final void run() {
        this.f$0.m1788lambda$processSelectedOption$196$orgtelegramuiChatActivity(this.f$1, this.f$2);
    }
}
