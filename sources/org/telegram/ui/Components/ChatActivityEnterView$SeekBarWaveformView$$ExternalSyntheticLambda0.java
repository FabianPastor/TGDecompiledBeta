package org.telegram.ui.Components;

import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.SeekBar;

public final /* synthetic */ class ChatActivityEnterView$SeekBarWaveformView$$ExternalSyntheticLambda0 implements SeekBar.SeekBarDelegate {
    public final /* synthetic */ ChatActivityEnterView.SeekBarWaveformView f$0;

    public /* synthetic */ ChatActivityEnterView$SeekBarWaveformView$$ExternalSyntheticLambda0(ChatActivityEnterView.SeekBarWaveformView seekBarWaveformView) {
        this.f$0 = seekBarWaveformView;
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public final void onSeekBarDrag(float f) {
        this.f$0.m709x426e2bc3(f);
    }
}
