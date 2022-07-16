package org.telegram.ui.Components;

import android.net.Uri;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ Uri f$1;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda7(BotWebViewContainer botWebViewContainer, Uri uri) {
        this.f$0 = botWebViewContainer;
        this.f$1 = uri;
    }

    public final void run() {
        this.f$0.lambda$onOpenUri$0(this.f$1);
    }
}
