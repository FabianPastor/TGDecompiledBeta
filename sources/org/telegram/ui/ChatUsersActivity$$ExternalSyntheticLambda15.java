package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda15 implements RequestDelegate {
    public final /* synthetic */ ChatUsersActivity f$0;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda15(ChatUsersActivity chatUsersActivity) {
        this.f$0 = chatUsersActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createMenuForParticipant$8(tLObject, tLRPC$TL_error);
    }
}
