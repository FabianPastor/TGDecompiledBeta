package org.telegram.ui.Components;

import android.net.Uri;

public final /* synthetic */ class ImageUpdater$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ImageUpdater f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ Uri f$2;

    public /* synthetic */ ImageUpdater$$ExternalSyntheticLambda1(ImageUpdater imageUpdater, String str, Uri uri) {
        this.f$0 = imageUpdater;
        this.f$1 = str;
        this.f$2 = uri;
    }

    public final void run() {
        this.f$0.lambda$startCrop$1(this.f$1, this.f$2);
    }
}
