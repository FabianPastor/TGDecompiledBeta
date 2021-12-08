package org.telegram.messenger;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ ImageLoader f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ ImageLocation f$3;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda13(ImageLoader imageLoader, String str, String str2, ImageLocation imageLocation) {
        this.f$0 = imageLoader;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = imageLocation;
    }

    public final void run() {
        this.f$0.m694lambda$replaceImageInCache$4$orgtelegrammessengerImageLoader(this.f$1, this.f$2, this.f$3);
    }
}
