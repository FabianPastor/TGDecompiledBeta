package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda149 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_messages_getWebPagePreview f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda149(ChatActivity chatActivity, TLRPC$TL_messages_getWebPagePreview tLRPC$TL_messages_getWebPagePreview) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_messages_getWebPagePreview;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchLinks$73(this.f$1, tLObject, tLRPC$TL_error);
    }
}
