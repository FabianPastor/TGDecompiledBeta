package org.telegram.messenger;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda5 implements ResultCallback {
    public static final /* synthetic */ ChatThemeController$$ExternalSyntheticLambda5 INSTANCE = new ChatThemeController$$ExternalSyntheticLambda5();

    private /* synthetic */ ChatThemeController$$ExternalSyntheticLambda5() {
    }

    public final void onComplete(Object obj) {
        ChatThemeController.lambda$preloadAllWallpaperImages$4((Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}
