package org.telegram.messenger;

import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda4(ResultCallback resultCallback, TLRPC.TL_error tL_error) {
        this.f$0 = resultCallback;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.onError(this.f$1);
    }
}
