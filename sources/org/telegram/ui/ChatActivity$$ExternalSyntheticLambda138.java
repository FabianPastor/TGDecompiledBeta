package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda138 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_messages_requestUrlAuth f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda138(ChatActivity chatActivity, String str, TLRPC.TL_messages_requestUrlAuth tL_messages_requestUrlAuth, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = str;
        this.f$2 = tL_messages_requestUrlAuth;
        this.f$3 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3108lambda$showRequestUrlAlert$231$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
