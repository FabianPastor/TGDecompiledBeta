package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda7(BotWebViewContainer botWebViewContainer, String str) {
        this.f$0 = botWebViewContainer;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onEventReceived$8(this.f$1, tLObject, tLRPC$TL_error);
    }
}
