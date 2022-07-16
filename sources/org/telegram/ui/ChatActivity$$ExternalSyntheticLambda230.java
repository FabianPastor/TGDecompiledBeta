package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda230 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda230(ChatActivity chatActivity, TLRPC$User tLRPC$User) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$User;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onTransitionAnimationEnd$135(this.f$1, tLObject, tLRPC$TL_error);
    }
}
