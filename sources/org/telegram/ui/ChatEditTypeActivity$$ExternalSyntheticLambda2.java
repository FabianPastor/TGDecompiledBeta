package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda2(ChatEditTypeActivity chatEditTypeActivity, TLRPC.TL_error tL_error) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m3225lambda$onFragmentCreate$0$orgtelegramuiChatEditTypeActivity(this.f$1);
    }
}
