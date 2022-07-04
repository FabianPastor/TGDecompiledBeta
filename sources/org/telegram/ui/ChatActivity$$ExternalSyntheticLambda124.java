package org.telegram.ui;

import android.content.Context;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda124 implements MessagesController.ErrorDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda124(ChatActivity chatActivity, Context context) {
        this.f$0 = chatActivity;
        this.f$1 = context;
    }

    public final boolean run(TLRPC.TL_error tL_error) {
        return this.f$0.m2989lambda$createView$70$orgtelegramuiChatActivity(this.f$1, tL_error);
    }
}
