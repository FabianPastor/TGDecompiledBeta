package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$QQrxxTOTOPgi4Ibzj2dcFh6tMmY implements Runnable {
    private final /* synthetic */ ImageLoader f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ ImageReceiver f$2;

    public /* synthetic */ -$$Lambda$ImageLoader$QQrxxTOTOPgi4Ibzj2dcFh6tMmY(ImageLoader imageLoader, boolean z, ImageReceiver imageReceiver) {
        this.f$0 = imageLoader;
        this.f$1 = z;
        this.f$2 = imageReceiver;
    }

    public final void run() {
        this.f$0.lambda$cancelLoadingForImageReceiver$2$ImageLoader(this.f$1, this.f$2);
    }
}
