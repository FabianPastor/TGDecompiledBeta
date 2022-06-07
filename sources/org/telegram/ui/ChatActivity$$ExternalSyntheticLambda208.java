package org.telegram.ui;

import android.content.Context;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda208 implements MessagesController.ErrorDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda208(ChatActivity chatActivity, Context context) {
        this.f$0 = chatActivity;
        this.f$1 = context;
    }

    public final boolean run(TLRPC$TL_error tLRPC$TL_error) {
        return this.f$0.lambda$createView$70(this.f$1, tLRPC$TL_error);
    }
}
