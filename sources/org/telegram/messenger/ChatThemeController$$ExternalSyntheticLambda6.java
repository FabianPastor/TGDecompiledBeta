package org.telegram.messenger;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda6 implements ResultCallback {
    public static final /* synthetic */ ChatThemeController$$ExternalSyntheticLambda6 INSTANCE = new ChatThemeController$$ExternalSyntheticLambda6();

    private /* synthetic */ ChatThemeController$$ExternalSyntheticLambda6() {
    }

    public final void onComplete(Object obj) {
        ChatThemeController.lambda$preloadAllWallpaperThumbs$4((Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}
