package org.telegram.ui.ActionBar;

import android.graphics.Bitmap;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$WallPaper;

public final /* synthetic */ class ChatTheme$$ExternalSyntheticLambda3 implements ResultCallback {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$WallPaper f$2;

    public /* synthetic */ ChatTheme$$ExternalSyntheticLambda3(ResultCallback resultCallback, long j, TLRPC$WallPaper tLRPC$WallPaper) {
        this.f$0 = resultCallback;
        this.f$1 = j;
        this.f$2 = tLRPC$WallPaper;
    }

    public final void onComplete(Object obj) {
        ChatTheme.lambda$loadWallpaper$1(this.f$0, this.f$1, this.f$2, (Bitmap) obj);
    }

    public /* synthetic */ void onError(TLRPC$TL_error tLRPC$TL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tLRPC$TL_error);
    }
}
