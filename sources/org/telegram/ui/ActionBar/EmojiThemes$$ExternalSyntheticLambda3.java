package org.telegram.ui.ActionBar;

import android.graphics.Bitmap;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class EmojiThemes$$ExternalSyntheticLambda3 implements ResultCallback {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.WallPaper f$2;

    public /* synthetic */ EmojiThemes$$ExternalSyntheticLambda3(ResultCallback resultCallback, long j, TLRPC.WallPaper wallPaper) {
        this.f$0 = resultCallback;
        this.f$1 = j;
        this.f$2 = wallPaper;
    }

    public final void onComplete(Object obj) {
        EmojiThemes.lambda$loadWallpaper$1(this.f$0, this.f$1, this.f$2, (Bitmap) obj);
    }

    public /* synthetic */ void onError(Throwable th) {
        ResultCallback.CC.$default$onError((ResultCallback) this, th);
    }

    public /* synthetic */ void onError(TLRPC.TL_error tL_error) {
        ResultCallback.CC.$default$onError((ResultCallback) this, tL_error);
    }
}
