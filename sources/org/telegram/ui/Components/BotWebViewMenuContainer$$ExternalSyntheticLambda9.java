package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BotWebViewMenuContainer$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ BotWebViewMenuContainer f$0;

    public /* synthetic */ BotWebViewMenuContainer$$ExternalSyntheticLambda9(BotWebViewMenuContainer botWebViewMenuContainer) {
        this.f$0 = botWebViewMenuContainer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadWebView$11(tLObject, tLRPC$TL_error);
    }
}
