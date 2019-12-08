package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$r2lkfi3DVCgGL1Psph_wyl_RwRo implements SeekBarDelegate {
    private final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ -$$Lambda$ArticleViewer$r2lkfi3DVCgGL1Psph_wyl_RwRo(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        -CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.lambda$setParentActivity$22$ArticleViewer(f);
    }
}
