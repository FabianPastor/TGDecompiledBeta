package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;

final /* synthetic */ class ArticleViewer$$Lambda$16 implements SeekBarDelegate {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$16(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$setParentActivity$19$ArticleViewer(f);
    }
}
