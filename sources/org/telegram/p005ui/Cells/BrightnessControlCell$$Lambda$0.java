package org.telegram.p005ui.Cells;

import org.telegram.p005ui.Components.SeekBarView.SeekBarViewDelegate;

/* renamed from: org.telegram.ui.Cells.BrightnessControlCell$$Lambda$0 */
final /* synthetic */ class BrightnessControlCell$$Lambda$0 implements SeekBarViewDelegate {
    private final BrightnessControlCell arg$1;

    BrightnessControlCell$$Lambda$0(BrightnessControlCell brightnessControlCell) {
        this.arg$1 = brightnessControlCell;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.didChangedValue(f);
    }
}
