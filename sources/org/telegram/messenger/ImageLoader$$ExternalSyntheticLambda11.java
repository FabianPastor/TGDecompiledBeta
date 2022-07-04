package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ ImageLoader f$0;
    public final /* synthetic */ ImageLoader.HttpFileTask f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda11(ImageLoader imageLoader, ImageLoader.HttpFileTask httpFileTask, int i) {
        this.f$0 = imageLoader;
        this.f$1 = httpFileTask;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$runHttpFileLoadTasks$13(this.f$1, this.f$2);
    }
}
