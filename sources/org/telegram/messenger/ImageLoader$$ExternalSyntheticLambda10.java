package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ImageLoader f$0;
    public final /* synthetic */ ImageLoader.HttpFileTask f$1;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda10(ImageLoader imageLoader, ImageLoader.HttpFileTask httpFileTask) {
        this.f$0 = imageLoader;
        this.f$1 = httpFileTask;
    }

    public final void run() {
        this.f$0.lambda$runHttpFileLoadTasks$12(this.f$1);
    }
}
