package org.telegram.messenger;

import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ TLObject f$0;
    public final /* synthetic */ ResultCallback f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda5(TLObject tLObject, ResultCallback resultCallback, TLRPC.TL_error tL_error, boolean z) {
        this.f$0 = tLObject;
        this.f$1 = resultCallback;
        this.f$2 = tL_error;
        this.f$3 = z;
    }

    public final void run() {
        ChatThemeController.lambda$requestAllChatThemes$2(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
