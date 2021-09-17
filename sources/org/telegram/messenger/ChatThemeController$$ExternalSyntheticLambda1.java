package org.telegram.messenger;

import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda1(ResultCallback resultCallback, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = resultCallback;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.onError(this.f$1);
    }
}
