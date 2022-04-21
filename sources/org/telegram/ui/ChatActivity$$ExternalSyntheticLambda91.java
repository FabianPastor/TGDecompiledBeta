package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda91 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda91(ChatActivity chatActivity, TLRPC.TL_error tL_error, TLRPC.User user) {
        this.f$0 = chatActivity;
        this.f$1 = tL_error;
        this.f$2 = user;
    }

    public final void run() {
        this.f$0.m1753lambda$onTransitionAnimationEnd$128$orgtelegramuiChatActivity(this.f$1, this.f$2);
    }
}
