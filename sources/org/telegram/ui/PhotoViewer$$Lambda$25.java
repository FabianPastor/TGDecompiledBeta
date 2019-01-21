package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;

final /* synthetic */ class PhotoViewer$$Lambda$25 implements SeekBarDelegate {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$25(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$createVideoControlsInterface$31$PhotoViewer(f);
    }
}
