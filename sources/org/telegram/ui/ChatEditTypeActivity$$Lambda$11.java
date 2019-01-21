package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$11 implements RequestDelegate {
    private final ChatEditTypeActivity arg$1;
    private final boolean arg$2;

    ChatEditTypeActivity$$Lambda$11(ChatEditTypeActivity chatEditTypeActivity, boolean z) {
        this.arg$1 = chatEditTypeActivity;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$generateLink$21$ChatEditTypeActivity(this.arg$2, tLObject, tL_error);
    }
}
