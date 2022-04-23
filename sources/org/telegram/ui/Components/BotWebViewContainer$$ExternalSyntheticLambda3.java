package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ BotWebViewContainer f$0;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda3(BotWebViewContainer botWebViewContainer) {
        this.f$0 = botWebViewContainer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadFlicker$2(tLObject, tLRPC$TL_error);
    }
}
