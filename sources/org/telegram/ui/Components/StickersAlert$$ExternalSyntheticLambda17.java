package org.telegram.ui.Components;

import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ SendMessagesHelper.ImportingSticker f$2;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda17(StickersAlert stickersAlert, String str, SendMessagesHelper.ImportingSticker importingSticker) {
        this.f$0 = stickersAlert;
        this.f$1 = str;
        this.f$2 = importingSticker;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$32(this.f$1, this.f$2);
    }
}
