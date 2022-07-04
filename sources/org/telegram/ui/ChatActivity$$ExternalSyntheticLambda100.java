package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda100 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_messages_getWebPagePreview f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda100(ChatActivity chatActivity, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
        this.f$0 = chatActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_getWebPagePreview;
    }

    public final void run() {
        this.f$0.m3089lambda$searchLinks$98$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
