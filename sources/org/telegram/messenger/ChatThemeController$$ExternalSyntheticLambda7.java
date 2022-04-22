package org.telegram.messenger;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda7 implements ResultCallback {
    public static final /* synthetic */ ChatThemeController$$ExternalSyntheticLambda7 INSTANCE = new ChatThemeController$$ExternalSyntheticLambda7();

    private /* synthetic */ ChatThemeController$$ExternalSyntheticLambda7() {
    }

    public final void onComplete(Object obj) {
        ChatThemeController.lambda$preloadAllWallpaperThumbs$4((Pair) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}
