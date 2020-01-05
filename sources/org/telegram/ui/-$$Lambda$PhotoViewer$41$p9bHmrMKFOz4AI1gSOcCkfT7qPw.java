package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.PhotoViewer.AnonymousClass41;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$41$p9bHmrMKFOz4AI1gSOcCkfT7qPw implements Runnable {
    private final /* synthetic */ AnonymousClass41 f$0;
    private final /* synthetic */ ClippingImageView[] f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$PhotoViewer$41$p9bHmrMKFOz4AI1gSOcCkfT7qPw(AnonymousClass41 anonymousClass41, ClippingImageView[] clippingImageViewArr, ArrayList arrayList) {
        this.f$0 = anonymousClass41;
        this.f$1 = clippingImageViewArr;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$onPreDraw$0$PhotoViewer$41(this.f$1, this.f$2);
    }
}
