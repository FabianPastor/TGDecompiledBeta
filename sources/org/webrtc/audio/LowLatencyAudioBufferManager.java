package org.webrtc.audio;

import android.media.AudioTrack;
import android.os.Build;
import org.webrtc.Logging;

class LowLatencyAudioBufferManager {
    private static final String TAG = "LowLatencyAudioBufferManager";
    private int bufferIncreaseCounter = 0;
    private boolean keepLoweringBufferSize = true;
    private int prevUnderrunCount = 0;
    private int ticksUntilNextDecrease = 10;

    public void maybeAdjustBufferSize(AudioTrack audioTrack) {
        if (Build.VERSION.SDK_INT >= 26) {
            int underrunCount = audioTrack.getUnderrunCount();
            if (underrunCount > this.prevUnderrunCount) {
                if (this.bufferIncreaseCounter < 5) {
                    int currentBufferSize = audioTrack.getBufferSizeInFrames();
                    int newBufferSize = (audioTrack.getPlaybackRate() / 100) + currentBufferSize;
                    Logging.d("LowLatencyAudioBufferManager", "Underrun detected! Increasing AudioTrack buffer size from " + currentBufferSize + " to " + newBufferSize);
                    audioTrack.setBufferSizeInFrames(newBufferSize);
                    this.bufferIncreaseCounter = this.bufferIncreaseCounter + 1;
                }
                this.keepLoweringBufferSize = false;
                this.prevUnderrunCount = underrunCount;
                this.ticksUntilNextDecrease = 10;
            } else if (this.keepLoweringBufferSize) {
                int i = this.ticksUntilNextDecrease - 1;
                this.ticksUntilNextDecrease = i;
                if (i <= 0) {
                    int bufferSize10ms = audioTrack.getPlaybackRate() / 100;
                    int currentBufferSize2 = audioTrack.getBufferSizeInFrames();
                    int newBufferSize2 = Math.max(bufferSize10ms, currentBufferSize2 - bufferSize10ms);
                    if (newBufferSize2 != currentBufferSize2) {
                        Logging.d("LowLatencyAudioBufferManager", "Lowering AudioTrack buffer size from " + currentBufferSize2 + " to " + newBufferSize2);
                        audioTrack.setBufferSizeInFrames(newBufferSize2);
                    }
                    this.ticksUntilNextDecrease = 10;
                }
            }
        }
    }
}
