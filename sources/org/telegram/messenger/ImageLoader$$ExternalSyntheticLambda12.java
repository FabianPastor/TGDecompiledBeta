package org.telegram.messenger;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ ImageLoader f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ImageReceiver f$2;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda12(ImageLoader imageLoader, boolean z, ImageReceiver imageReceiver) {
        this.f$0 = imageLoader;
        this.f$1 = z;
        this.f$2 = imageReceiver;
    }

    public final void run() {
        this.f$0.lambda$cancelLoadingForImageReceiver$3(this.f$1, this.f$2);
    }
}
