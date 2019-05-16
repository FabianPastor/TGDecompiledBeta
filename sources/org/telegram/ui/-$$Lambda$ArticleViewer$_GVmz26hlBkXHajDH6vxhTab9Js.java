package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$_GVmz26hlBkXHajDH6vxhTab9Js implements SeekBarDelegate {
    private final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ -$$Lambda$ArticleViewer$_GVmz26hlBkXHajDH6vxhTab9Js(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.lambda$setParentActivity$19$ArticleViewer(f);
    }
}
