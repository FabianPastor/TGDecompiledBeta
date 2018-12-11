package org.telegram.p005ui.Cells;

import org.telegram.p005ui.Components.SeekBarView.SeekBarViewDelegate;

/* renamed from: org.telegram.ui.Cells.MaxFileSizeCell$$Lambda$0 */
final /* synthetic */ class MaxFileSizeCell$$Lambda$0 implements SeekBarViewDelegate {
    private final MaxFileSizeCell arg$1;

    MaxFileSizeCell$$Lambda$0(MaxFileSizeCell maxFileSizeCell) {
        this.arg$1 = maxFileSizeCell;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$new$0$MaxFileSizeCell(f);
    }
}
