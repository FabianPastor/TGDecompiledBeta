package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda10(BotWebViewContainer botWebViewContainer, TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject) {
        this.f$0 = botWebViewContainer;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = str;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$onEventReceived$11(this.f$1, this.f$2, this.f$3);
    }
}
