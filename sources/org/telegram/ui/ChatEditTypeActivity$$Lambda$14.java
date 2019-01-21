package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$14 implements RequestDelegate {
    private final ChatEditTypeActivity arg$1;
    private final String arg$2;

    ChatEditTypeActivity$$Lambda$14(ChatEditTypeActivity chatEditTypeActivity, String str) {
        this.arg$1 = chatEditTypeActivity;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$18$ChatEditTypeActivity(this.arg$2, tLObject, tL_error);
    }
}
