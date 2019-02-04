package org.telegram.ui;

import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

final /* synthetic */ class ThemeActivity$TextSizeCell$$Lambda$0 implements SeekBarViewDelegate {
    private final TextSizeCell arg$1;

    ThemeActivity$TextSizeCell$$Lambda$0(TextSizeCell textSizeCell) {
        this.arg$1 = textSizeCell;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$new$0$ThemeActivity$TextSizeCell(f);
    }
}
