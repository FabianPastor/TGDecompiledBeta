package org.telegram.ui;

import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Components.SeekBar;

public final /* synthetic */ class ArticleViewer$BlockAudioCell$$ExternalSyntheticLambda0 implements SeekBar.SeekBarDelegate {
    public final /* synthetic */ ArticleViewer.BlockAudioCell f$0;

    public /* synthetic */ ArticleViewer$BlockAudioCell$$ExternalSyntheticLambda0(ArticleViewer.BlockAudioCell blockAudioCell) {
        this.f$0 = blockAudioCell;
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.m1414lambda$new$0$orgtelegramuiArticleViewer$BlockAudioCell(f);
    }
}
