package org.telegram.ui;

import com.coremedia.iso.boxes.TrackHeaderBox;
import org.telegram.ui.PhotoViewer.AnonymousClass43;

final /* synthetic */ class PhotoViewer$43$$Lambda$0 implements Runnable {
    private final AnonymousClass43 arg$1;
    private final boolean arg$2;
    private final TrackHeaderBox arg$3;

    PhotoViewer$43$$Lambda$0(AnonymousClass43 anonymousClass43, boolean z, TrackHeaderBox trackHeaderBox) {
        this.arg$1 = anonymousClass43;
        this.arg$2 = z;
        this.arg$3 = trackHeaderBox;
    }

    public void run() {
        this.arg$1.lambda$run$0$PhotoViewer$43(this.arg$2, this.arg$3);
    }
}
