package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.BotWebViewMenuContainer;

public final /* synthetic */ class BotWebViewMenuContainer$1$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ BotWebViewMenuContainer.AnonymousClass1 f$0;

    public /* synthetic */ BotWebViewMenuContainer$1$$ExternalSyntheticLambda2(BotWebViewMenuContainer.AnonymousClass1 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onSendWebViewData$1(tLObject, tLRPC$TL_error);
    }
}
