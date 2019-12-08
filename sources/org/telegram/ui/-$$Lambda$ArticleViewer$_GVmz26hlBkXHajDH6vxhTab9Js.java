package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$_GVmz26hlBkXHajDH6vxhTab9Js implements SeekBarDelegate {
    private final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ -$$Lambda$ArticleViewer$_GVmz26hlBkXHajDH6vxhTab9Js(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        -CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.lambda$setParentActivity$19$ArticleViewer(f);
    }
}
