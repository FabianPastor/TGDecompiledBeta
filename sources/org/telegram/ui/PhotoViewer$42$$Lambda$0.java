package org.telegram.ui;

import com.coremedia.iso.boxes.TrackHeaderBox;
import org.telegram.ui.PhotoViewer.AnonymousClass42;

final /* synthetic */ class PhotoViewer$42$$Lambda$0 implements Runnable {
    private final AnonymousClass42 arg$1;
    private final boolean arg$2;
    private final TrackHeaderBox arg$3;

    PhotoViewer$42$$Lambda$0(AnonymousClass42 anonymousClass42, boolean z, TrackHeaderBox trackHeaderBox) {
        this.arg$1 = anonymousClass42;
        this.arg$2 = z;
        this.arg$3 = trackHeaderBox;
    }

    public void run() {
        this.arg$1.lambda$run$0$PhotoViewer$42(this.arg$2, this.arg$3);
    }
}
