package org.telegram.messenger;

import android.graphics.Bitmap;
import org.telegram.tgnet.ResultCallback;

public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ Bitmap f$1;

    public /* synthetic */ ChatThemeController$$ExternalSyntheticLambda3(ResultCallback resultCallback, Bitmap bitmap) {
        this.f$0 = resultCallback;
        this.f$1 = bitmap;
    }

    public final void run() {
        this.f$0.onComplete(this.f$1);
    }
}
