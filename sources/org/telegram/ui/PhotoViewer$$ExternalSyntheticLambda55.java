package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ ImageReceiver.BitmapHolder f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda55(PhotoViewer photoViewer, ImageReceiver.BitmapHolder bitmapHolder, String str) {
        this.f$0 = photoViewer;
        this.f$1 = bitmapHolder;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$detectFaces$48(this.f$1, this.f$2);
    }
}
