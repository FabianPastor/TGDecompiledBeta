package org.telegram.messenger;

import org.telegram.tgnet.ResultCallback;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ ResultCallback f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda4(boolean z, ResultCallback resultCallback) {
        this.f$0 = z;
        this.f$1 = resultCallback;
    }

    public final void run() {
        ChatThemeController.lambda$requestAllChatThemes$5(this.f$0, this.f$1);
    }
}
