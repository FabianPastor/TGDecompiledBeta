package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$3J3laPfRGnIFxoDAtOMOxH-OzjM implements SeekBarDelegate {
    private final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ -$$Lambda$PhotoViewer$3J3laPfRGnIFxoDAtOMOxH-OzjM(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.lambda$createVideoControlsInterface$34$PhotoViewer(f);
    }
}
