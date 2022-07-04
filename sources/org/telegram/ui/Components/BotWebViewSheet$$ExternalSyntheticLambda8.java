package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ BotWebViewSheet f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda8(BotWebViewSheet botWebViewSheet, TLObject tLObject, int i) {
        this.f$0 = botWebViewSheet;
        this.f$1 = tLObject;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$requestWebView$17(this.f$1, this.f$2);
    }
}
