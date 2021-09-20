package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda3(ResultCallback resultCallback, boolean z) {
        this.f$0 = resultCallback;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatThemeController.chatThemeQueue.postRunnable(new ChatThemeController$$ExternalSyntheticLambda2(tLObject, this.f$0, tLRPC$TL_error, this.f$1));
    }
}
