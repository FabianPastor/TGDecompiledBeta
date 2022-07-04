package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.BotWebViewSheet;

public final /* synthetic */ class BotWebViewSheet$2$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ BotWebViewSheet.AnonymousClass2 f$0;

    public /* synthetic */ BotWebViewSheet$2$$ExternalSyntheticLambda3(BotWebViewSheet.AnonymousClass2 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onSendWebViewData$0(tLObject, tLRPC$TL_error);
    }
}
