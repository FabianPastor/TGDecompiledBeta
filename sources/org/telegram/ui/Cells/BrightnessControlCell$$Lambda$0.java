package org.telegram.ui.Cells;

import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

final /* synthetic */ class BrightnessControlCell$$Lambda$0 implements SeekBarViewDelegate {
    private final BrightnessControlCell arg$1;

    BrightnessControlCell$$Lambda$0(BrightnessControlCell brightnessControlCell) {
        this.arg$1 = brightnessControlCell;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.didChangedValue(f);
    }
}
