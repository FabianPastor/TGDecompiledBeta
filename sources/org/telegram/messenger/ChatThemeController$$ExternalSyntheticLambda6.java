package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda6(ResultCallback resultCallback, boolean z) {
        this.f$0 = resultCallback;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChatThemeController.chatThemeQueue.postRunnable(new ChatThemeController$$ExternalSyntheticLambda5(tLObject, this.f$0, tL_error, this.f$1));
    }
}
