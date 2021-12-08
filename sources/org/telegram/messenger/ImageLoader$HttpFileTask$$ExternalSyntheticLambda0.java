package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$HttpFileTask$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ImageLoader.HttpFileTask f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ ImageLoader$HttpFileTask$$ExternalSyntheticLambda0(ImageLoader.HttpFileTask httpFileTask, long j, long j2) {
        this.f$0 = httpFileTask;
        this.f$1 = j;
        this.f$2 = j2;
    }

    public final void run() {
        this.f$0.lambda$reportProgress$1(this.f$1, this.f$2);
    }
}
