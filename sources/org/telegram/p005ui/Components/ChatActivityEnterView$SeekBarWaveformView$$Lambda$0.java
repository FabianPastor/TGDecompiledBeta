package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.ChatActivityEnterView.SeekBarWaveformView;
import org.telegram.p005ui.Components.SeekBar.SeekBarDelegate;

/* renamed from: org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView$$Lambda$0 */
final /* synthetic */ class ChatActivityEnterView$SeekBarWaveformView$$Lambda$0 implements SeekBarDelegate {
    private final SeekBarWaveformView arg$1;

    ChatActivityEnterView$SeekBarWaveformView$$Lambda$0(SeekBarWaveformView seekBarWaveformView) {
        this.arg$1 = seekBarWaveformView;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$new$0$ChatActivityEnterView$SeekBarWaveformView(f);
    }
}
