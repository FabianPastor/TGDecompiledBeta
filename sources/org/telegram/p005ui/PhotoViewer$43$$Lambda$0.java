package org.telegram.p005ui;

import com.coremedia.iso.boxes.TrackHeaderBox;
import org.telegram.p005ui.PhotoViewer.C110243;

/* renamed from: org.telegram.ui.PhotoViewer$43$$Lambda$0 */
final /* synthetic */ class PhotoViewer$43$$Lambda$0 implements Runnable {
    private final C110243 arg$1;
    private final boolean arg$2;
    private final TrackHeaderBox arg$3;

    PhotoViewer$43$$Lambda$0(C110243 c110243, boolean z, TrackHeaderBox trackHeaderBox) {
        this.arg$1 = c110243;
        this.arg$2 = z;
        this.arg$3 = trackHeaderBox;
    }

    public void run() {
        this.arg$1.lambda$run$0$PhotoViewer$43(this.arg$2, this.arg$3);
    }
}
