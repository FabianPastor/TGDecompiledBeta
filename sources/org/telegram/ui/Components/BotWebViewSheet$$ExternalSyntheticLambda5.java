package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ BotWebViewSheet f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda5(BotWebViewSheet botWebViewSheet, TLObject tLObject, int i, long j) {
        this.f$0 = botWebViewSheet;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$requestWebView$9(this.f$1, this.f$2, this.f$3);
    }
}
