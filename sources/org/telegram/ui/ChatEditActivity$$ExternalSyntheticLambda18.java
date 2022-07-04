package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ ChatEditActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda18(ChatEditActivity chatEditActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = chatEditActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3205lambda$loadLinksCount$0$orgtelegramuiChatEditActivity(this.f$1, this.f$2);
    }
}
