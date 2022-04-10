package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ BotWebViewSheet f$0;

    public /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda11(BotWebViewSheet botWebViewSheet) {
        this.f$0 = botWebViewSheet;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$requestWebView$15(tLObject, tLRPC$TL_error);
    }
}
