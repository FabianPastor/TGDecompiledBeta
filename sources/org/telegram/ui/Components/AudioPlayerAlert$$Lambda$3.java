package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

final /* synthetic */ class AudioPlayerAlert$$Lambda$3 implements SeekBarViewDelegate {
    static final SeekBarViewDelegate $instance = new AudioPlayerAlert$$Lambda$3();

    private AudioPlayerAlert$$Lambda$3() {
    }

    public void onSeekBarDrag(float f) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), f);
    }
}
