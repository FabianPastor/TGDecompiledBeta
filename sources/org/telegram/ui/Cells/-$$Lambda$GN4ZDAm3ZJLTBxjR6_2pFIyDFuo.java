package org.telegram.ui.Cells;

import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GN4ZDAm3ZJLTBxjR6_2pFIyDFuo implements SeekBarViewDelegate {
    private final /* synthetic */ BrightnessControlCell f$0;

    public /* synthetic */ -$$Lambda$GN4ZDAm3ZJLTBxjR6_2pFIyDFuo(BrightnessControlCell brightnessControlCell) {
        this.f$0 = brightnessControlCell;
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.didChangedValue(f);
    }
}
