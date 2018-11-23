package org.telegram.p005ui;

import org.telegram.p005ui.Components.SeekBar.SeekBarDelegate;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$16 */
final /* synthetic */ class ArticleViewer$$Lambda$16 implements SeekBarDelegate {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$16(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$setParentActivity$19$ArticleViewer(f);
    }
}
