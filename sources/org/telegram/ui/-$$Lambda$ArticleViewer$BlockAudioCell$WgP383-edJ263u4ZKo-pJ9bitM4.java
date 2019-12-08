package org.telegram.ui;

import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$BlockAudioCell$WgP383-edJ263u4ZKo-pJ9bitM4 implements SeekBarDelegate {
    private final /* synthetic */ BlockAudioCell f$0;

    public /* synthetic */ -$$Lambda$ArticleViewer$BlockAudioCell$WgP383-edJ263u4ZKo-pJ9bitM4(BlockAudioCell blockAudioCell) {
        this.f$0 = blockAudioCell;
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        -CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.lambda$new$0$ArticleViewer$BlockAudioCell(f);
    }
}
