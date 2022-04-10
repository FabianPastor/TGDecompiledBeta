package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout f$0;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda10(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout) {
        this.f$0 = chatAttachAlertBotWebViewLayout;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$requestWebView$10(tLObject, tLRPC$TL_error);
    }
}
