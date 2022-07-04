package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda3(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, int i) {
        this.f$0 = chatAttachAlertBotWebViewLayout;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m771x3b4af8c0(this.f$1, tLObject, tL_error);
    }
}
