package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda6(BotWebViewContainer botWebViewContainer, TLRPC.TL_error tL_error, String str, TLObject tLObject) {
        this.f$0 = botWebViewContainer;
        this.f$1 = tL_error;
        this.f$2 = str;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m579x43a33CLASSNAME(this.f$1, this.f$2, this.f$3);
    }
}
