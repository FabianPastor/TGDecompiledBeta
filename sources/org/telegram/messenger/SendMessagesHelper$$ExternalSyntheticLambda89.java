package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda89 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_messages_requestUrlAuth f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda89(ChatActivity chatActivity, TLRPC.TL_messages_requestUrlAuth tL_messages_requestUrlAuth, String str, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_requestUrlAuth;
        this.f$2 = str;
        this.f$3 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        SendMessagesHelper.lambda$requestUrlAuth$23(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
