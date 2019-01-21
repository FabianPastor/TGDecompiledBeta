package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$1 implements RequestDelegate {
    private final ChatEditTypeActivity arg$1;

    ChatEditTypeActivity$$Lambda$1(ChatEditTypeActivity chatEditTypeActivity) {
        this.arg$1 = chatEditTypeActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onFragmentCreate$2$ChatEditTypeActivity(tLObject, tL_error);
    }
}
