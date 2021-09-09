package org.telegram.messenger;

import java.util.List;
import org.telegram.tgnet.ResultCallback;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ List f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda0(ResultCallback resultCallback, List list) {
        this.f$0 = resultCallback;
        this.f$1 = list;
    }

    public final void run() {
        this.f$0.onComplete(this.f$1);
    }
}
