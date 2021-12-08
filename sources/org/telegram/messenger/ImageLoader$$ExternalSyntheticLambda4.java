package org.telegram.messenger;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ImageLoader f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda4(ImageLoader imageLoader, String str) {
        this.f$0 = imageLoader;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$cancelForceLoadingForImageReceiver$5(this.f$1);
    }
}
