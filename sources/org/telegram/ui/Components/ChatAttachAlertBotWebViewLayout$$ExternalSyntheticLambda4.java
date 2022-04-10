package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda4(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, TLObject tLObject, int i, long j) {
        this.f$0 = chatAttachAlertBotWebViewLayout;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$requestWebView$5(this.f$1, this.f$2, this.f$3);
    }
}
