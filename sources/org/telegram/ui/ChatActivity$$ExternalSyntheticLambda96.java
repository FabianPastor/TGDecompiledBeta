package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda96 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.TL_messages_requestUrlAuth f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda96(ChatActivity chatActivity, TLObject tLObject, String str, TLRPC.TL_messages_requestUrlAuth tL_messages_requestUrlAuth, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = tLObject;
        this.f$2 = str;
        this.f$3 = tL_messages_requestUrlAuth;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.m3107lambda$showRequestUrlAlert$230$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
