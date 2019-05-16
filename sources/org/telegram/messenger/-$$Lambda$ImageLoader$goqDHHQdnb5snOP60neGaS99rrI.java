package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$goqDHHQdnb5snOP60neGaS99rrI implements Runnable {
    private final /* synthetic */ ImageLoader f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ ImageLocation f$3;

    public /* synthetic */ -$$Lambda$ImageLoader$goqDHHQdnb5snOP60neGaS99rrI(ImageLoader imageLoader, String str, String str2, ImageLocation imageLocation) {
        this.f$0 = imageLoader;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = imageLocation;
    }

    public final void run() {
        this.f$0.lambda$replaceImageInCache$3$ImageLoader(this.f$1, this.f$2, this.f$3);
    }
}
