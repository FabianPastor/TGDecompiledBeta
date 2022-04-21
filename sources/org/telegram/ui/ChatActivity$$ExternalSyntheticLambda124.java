package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda124 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_messages_getWebPagePreview f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda124(ChatActivity chatActivity, TLRPC.TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_getWebPagePreview;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1806lambda$searchLinks$97$orgtelegramuiChatActivity(this.f$1, tLObject, tL_error);
    }
}
