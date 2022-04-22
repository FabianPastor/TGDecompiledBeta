package org.telegram.ui.Components;

import androidx.core.util.Consumer;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ Consumer f$1;
    public final /* synthetic */ String[] f$2;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda1(BotWebViewContainer botWebViewContainer, Consumer consumer, String[] strArr) {
        this.f$0 = botWebViewContainer;
        this.f$1 = consumer;
        this.f$2 = strArr;
    }

    public final void run() {
        this.f$0.lambda$runWithPermissions$0(this.f$1, this.f$2);
    }
}
