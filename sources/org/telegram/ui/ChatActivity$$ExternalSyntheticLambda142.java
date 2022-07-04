package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda142 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda142(ChatActivity chatActivity, TLRPC.User user) {
        this.f$0 = chatActivity;
        this.f$1 = user;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3035lambda$onTransitionAnimationEnd$132$orgtelegramuiChatActivity(this.f$1, tLObject, tL_error);
    }
}
