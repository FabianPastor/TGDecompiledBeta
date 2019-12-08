package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.PhotoViewer.AnonymousClass40;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$40$JKOAs93TNAIRxdBgHbufSN2SIzs implements Runnable {
    private final /* synthetic */ AnonymousClass40 f$0;
    private final /* synthetic */ ClippingImageView[] f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$PhotoViewer$40$JKOAs93TNAIRxdBgHbufSN2SIzs(AnonymousClass40 anonymousClass40, ClippingImageView[] clippingImageViewArr, ArrayList arrayList) {
        this.f$0 = anonymousClass40;
        this.f$1 = clippingImageViewArr;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$onPreDraw$0$PhotoViewer$40(this.f$1, this.f$2);
    }
}
