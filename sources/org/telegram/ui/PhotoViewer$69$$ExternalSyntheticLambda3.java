package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$69$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass69 f$0;
    public final /* synthetic */ ClippingImageView[] f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ Integer f$3;
    public final /* synthetic */ PhotoViewer.PhotoViewerProvider f$4;

    public /* synthetic */ PhotoViewer$69$$ExternalSyntheticLambda3(PhotoViewer.AnonymousClass69 r1, ClippingImageView[] clippingImageViewArr, ArrayList arrayList, Integer num, PhotoViewer.PhotoViewerProvider photoViewerProvider) {
        this.f$0 = r1;
        this.f$1 = clippingImageViewArr;
        this.f$2 = arrayList;
        this.f$3 = num;
        this.f$4 = photoViewerProvider;
    }

    public final void run() {
        this.f$0.lambda$onPreDraw$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
