package org.telegram.p005ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.p005ui.Components.SeekBarView.SeekBarViewDelegate;

/* renamed from: org.telegram.ui.Components.AudioPlayerAlert$$Lambda$3 */
final /* synthetic */ class AudioPlayerAlert$$Lambda$3 implements SeekBarViewDelegate {
    static final SeekBarViewDelegate $instance = new AudioPlayerAlert$$Lambda$3();

    private AudioPlayerAlert$$Lambda$3() {
    }

    public void onSeekBarDrag(float f) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), f);
    }
}
