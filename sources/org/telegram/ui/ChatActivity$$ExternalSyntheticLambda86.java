package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda86 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda86(ChatActivity chatActivity, TLObject tLObject, TLRPC.User user) {
        this.f$0 = chatActivity;
        this.f$1 = tLObject;
        this.f$2 = user;
    }

    public final void run() {
        this.f$0.m1756lambda$onTransitionAnimationEnd$131$orgtelegramuiChatActivity(this.f$1, this.f$2);
    }
}
