package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$62$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass62 f$0;
    public final /* synthetic */ ClippingImageView[] f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ Integer f$3;

    public /* synthetic */ PhotoViewer$62$$ExternalSyntheticLambda2(PhotoViewer.AnonymousClass62 r1, ClippingImageView[] clippingImageViewArr, ArrayList arrayList, Integer num) {
        this.f$0 = r1;
        this.f$1 = clippingImageViewArr;
        this.f$2 = arrayList;
        this.f$3 = num;
    }

    public final void run() {
        this.f$0.m3630lambda$onPreDraw$0$orgtelegramuiPhotoViewer$62(this.f$1, this.f$2, this.f$3);
    }
}
