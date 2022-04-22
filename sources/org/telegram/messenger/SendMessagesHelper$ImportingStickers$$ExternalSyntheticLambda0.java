package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class SendMessagesHelper$ImportingStickers$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SendMessagesHelper.ImportingStickers f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ SendMessagesHelper$ImportingStickers$$ExternalSyntheticLambda0(SendMessagesHelper.ImportingStickers importingStickers, String str) {
        this.f$0 = importingStickers;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$onMediaImport$0(this.f$1);
    }
}
