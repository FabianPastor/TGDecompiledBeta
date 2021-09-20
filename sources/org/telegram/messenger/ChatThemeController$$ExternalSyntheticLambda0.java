package org.telegram.messenger;

import java.util.List;
import org.telegram.tgnet.ResultCallback;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ List f$0;
    public final /* synthetic */ ResultCallback f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda0(List list, ResultCallback resultCallback) {
        this.f$0 = list;
        this.f$1 = resultCallback;
    }

    public final void run() {
        ChatThemeController.lambda$requestAllChatThemes$1(this.f$0, this.f$1);
    }
}
