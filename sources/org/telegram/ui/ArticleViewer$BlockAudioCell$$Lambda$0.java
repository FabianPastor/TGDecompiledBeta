package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;

final /* synthetic */ class ArticleViewer$BlockAudioCell$$Lambda$0 implements SeekBarDelegate {
    private final BlockAudioCell arg$1;

    ArticleViewer$BlockAudioCell$$Lambda$0(BlockAudioCell blockAudioCell) {
        this.arg$1 = blockAudioCell;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$new$0$ArticleViewer$BlockAudioCell(f);
    }
}
