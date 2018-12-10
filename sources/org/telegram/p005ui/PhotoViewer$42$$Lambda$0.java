package org.telegram.p005ui;

import com.coremedia.iso.boxes.TrackHeaderBox;
import org.telegram.p005ui.PhotoViewer.CLASSNAME;

/* renamed from: org.telegram.ui.PhotoViewer$42$$Lambda$0 */
final /* synthetic */ class PhotoViewer$42$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final boolean arg$2;
    private final TrackHeaderBox arg$3;

    PhotoViewer$42$$Lambda$0(CLASSNAME CLASSNAME, boolean z, TrackHeaderBox trackHeaderBox) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = z;
        this.arg$3 = trackHeaderBox;
    }

    public void run() {
        this.arg$1.lambda$run$0$PhotoViewer$42(this.arg$2, this.arg$3);
    }
}
