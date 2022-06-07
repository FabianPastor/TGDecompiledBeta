package org.telegram.ui;

import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$12$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass12 f$0;
    public final /* synthetic */ ShareAlert f$1;

    public /* synthetic */ PhotoViewer$12$$ExternalSyntheticLambda4(PhotoViewer.AnonymousClass12 r1, ShareAlert shareAlert) {
        this.f$0 = r1;
        this.f$1 = shareAlert;
    }

    public final void run() {
        this.f$0.lambda$onItemClick$1(this.f$1);
    }
}
