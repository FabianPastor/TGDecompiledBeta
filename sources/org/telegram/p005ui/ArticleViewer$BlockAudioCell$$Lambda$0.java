package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockAudioCell;
import org.telegram.p005ui.Components.SeekBar.SeekBarDelegate;

/* renamed from: org.telegram.ui.ArticleViewer$BlockAudioCell$$Lambda$0 */
final /* synthetic */ class ArticleViewer$BlockAudioCell$$Lambda$0 implements SeekBarDelegate {
    private final BlockAudioCell arg$1;

    ArticleViewer$BlockAudioCell$$Lambda$0(BlockAudioCell blockAudioCell) {
        this.arg$1 = blockAudioCell;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$new$0$ArticleViewer$BlockAudioCell(f);
    }
}
