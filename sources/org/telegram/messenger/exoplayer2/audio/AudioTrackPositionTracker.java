package org.telegram.messenger.exoplayer2.audio;

import android.media.AudioTrack;
import android.os.SystemClock;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import org.telegram.messenger.exoplayer2.C0559C;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class AudioTrackPositionTracker {
    private static final long FORCE_RESET_WORKAROUND_TIMEOUT_MS = 200;
    private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000;
    private static final long MAX_LATENCY_US = 5000000;
    private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
    private static final int MIN_LATENCY_SAMPLE_INTERVAL_US = 500000;
    private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
    private static final int PLAYSTATE_PAUSED = 2;
    private static final int PLAYSTATE_PLAYING = 3;
    private static final int PLAYSTATE_STOPPED = 1;
    private AudioTimestampPoller audioTimestampPoller;
    private AudioTrack audioTrack;
    private int bufferSize;
    private long bufferSizeUs;
    private long endPlaybackHeadPosition;
    private long forceResetWorkaroundTimeMs;
    private Method getLatencyMethod;
    private boolean hasData;
    private boolean isOutputPcm;
    private long lastLatencySampleTimeUs;
    private long lastPlayheadSampleTimeUs;
    private long lastRawPlaybackHeadPosition;
    private long latencyUs;
    private final Listener listener;
    private boolean needsPassthroughWorkarounds;
    private int nextPlayheadOffsetIndex;
    private int outputPcmFrameSize;
    private int outputSampleRate;
    private long passthroughWorkaroundPauseOffset;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private long rawPlaybackHeadWrapCount;
    private long smoothedPlayheadOffsetUs;
    private long stopPlaybackHeadPosition;
    private long stopTimestampUs;

    public interface Listener {
        void onInvalidLatency(long j);

        void onPositionFramesMismatch(long j, long j2, long j3, long j4);

        void onSystemTimeUsMismatch(long j, long j2, long j3, long j4);

        void onUnderrun(int i, long j);
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface PlayState {
    }

    public AudioTrackPositionTracker(Listener listener) {
        this.listener = (Listener) Assertions.checkNotNull(listener);
        if (Util.SDK_INT >= 18) {
            try {
                this.getLatencyMethod = AudioTrack.class.getMethod("getLatency", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
        }
        this.playheadOffsets = new long[10];
    }

    public void setAudioTrack(AudioTrack audioTrack, int outputEncoding, int outputPcmFrameSize, int bufferSize) {
        this.audioTrack = audioTrack;
        this.outputPcmFrameSize = outputPcmFrameSize;
        this.bufferSize = bufferSize;
        this.audioTimestampPoller = new AudioTimestampPoller(audioTrack);
        this.outputSampleRate = audioTrack.getSampleRate();
        this.needsPassthroughWorkarounds = needsPassthroughWorkarounds(outputEncoding);
        this.isOutputPcm = Util.isEncodingPcm(outputEncoding);
        this.bufferSizeUs = this.isOutputPcm ? framesToDurationUs((long) (bufferSize / outputPcmFrameSize)) : C0559C.TIME_UNSET;
        this.lastRawPlaybackHeadPosition = 0;
        this.rawPlaybackHeadWrapCount = 0;
        this.passthroughWorkaroundPauseOffset = 0;
        this.hasData = false;
        this.stopTimestampUs = C0559C.TIME_UNSET;
        this.forceResetWorkaroundTimeMs = C0559C.TIME_UNSET;
        this.latencyUs = 0;
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        if (this.audioTrack.getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long systemTimeUs = System.nanoTime() / 1000;
        if (this.audioTimestampPoller.hasTimestamp()) {
            long timestampPositionUs = framesToDurationUs(this.audioTimestampPoller.getTimestampPositionFrames());
            if (this.audioTimestampPoller.isTimestampAdvancing()) {
                return timestampPositionUs + (systemTimeUs - this.audioTimestampPoller.getTimestampSystemTimeUs());
            }
            return timestampPositionUs;
        }
        long positionUs;
        if (this.playheadOffsetCount == 0) {
            positionUs = getPlaybackHeadPositionUs();
        } else {
            positionUs = systemTimeUs + this.smoothedPlayheadOffsetUs;
        }
        if (!sourceEnded) {
            positionUs -= this.latencyUs;
        }
        return positionUs;
    }

    public void start() {
        this.audioTimestampPoller.reset();
    }

    public boolean isPlaying() {
        return this.audioTrack.getPlayState() == 3;
    }

    public boolean mayHandleBuffer(long writtenFrames) {
        int playState = this.audioTrack.getPlayState();
        if (this.needsPassthroughWorkarounds) {
            if (playState == 2) {
                this.hasData = false;
                return false;
            } else if (playState == 1 && getPlaybackHeadPosition() == 0) {
                return false;
            }
        }
        boolean hadData = this.hasData;
        this.hasData = hasPendingData(writtenFrames);
        if (!(!hadData || this.hasData || playState == 1 || this.listener == null)) {
            this.listener.onUnderrun(this.bufferSize, C0559C.usToMs(this.bufferSizeUs));
        }
        return true;
    }

    public int getAvailableBufferSize(long writtenBytes) {
        return this.bufferSize - ((int) (writtenBytes - (getPlaybackHeadPosition() * ((long) this.outputPcmFrameSize))));
    }

    public boolean isStalled(long writtenFrames) {
        return this.forceResetWorkaroundTimeMs != C0559C.TIME_UNSET && writtenFrames > 0 && SystemClock.elapsedRealtime() - this.forceResetWorkaroundTimeMs >= FORCE_RESET_WORKAROUND_TIMEOUT_MS;
    }

    public void handleEndOfStream(long writtenFrames) {
        this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
        this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
        this.endPlaybackHeadPosition = writtenFrames;
    }

    public boolean hasPendingData(long writtenFrames) {
        return writtenFrames > getPlaybackHeadPosition() || forceHasPendingData();
    }

    public boolean pause() {
        resetSyncParams();
        if (this.stopTimestampUs != C0559C.TIME_UNSET) {
            return false;
        }
        this.audioTimestampPoller.reset();
        return true;
    }

    public void reset() {
        resetSyncParams();
        this.audioTrack = null;
        this.audioTimestampPoller = null;
    }

    private void maybeSampleSyncParams() {
        long playbackPositionUs = getPlaybackHeadPositionUs();
        if (playbackPositionUs != 0) {
            long systemTimeUs = System.nanoTime() / 1000;
            if (systemTimeUs - this.lastPlayheadSampleTimeUs >= 30000) {
                this.playheadOffsets[this.nextPlayheadOffsetIndex] = playbackPositionUs - systemTimeUs;
                this.nextPlayheadOffsetIndex = (this.nextPlayheadOffsetIndex + 1) % 10;
                if (this.playheadOffsetCount < 10) {
                    this.playheadOffsetCount++;
                }
                this.lastPlayheadSampleTimeUs = systemTimeUs;
                this.smoothedPlayheadOffsetUs = 0;
                for (int i = 0; i < this.playheadOffsetCount; i++) {
                    this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / ((long) this.playheadOffsetCount);
                }
            }
            if (!this.needsPassthroughWorkarounds) {
                maybePollAndCheckTimestamp(systemTimeUs, playbackPositionUs);
                maybeUpdateLatency(systemTimeUs);
            }
        }
    }

    private void maybePollAndCheckTimestamp(long systemTimeUs, long playbackPositionUs) {
        if (this.audioTimestampPoller.maybePollTimestamp(systemTimeUs)) {
            long audioTimestampSystemTimeUs = this.audioTimestampPoller.getTimestampSystemTimeUs();
            long audioTimestampPositionFrames = this.audioTimestampPoller.getTimestampPositionFrames();
            if (Math.abs(audioTimestampSystemTimeUs - systemTimeUs) > 5000000) {
                this.listener.onSystemTimeUsMismatch(audioTimestampPositionFrames, audioTimestampSystemTimeUs, systemTimeUs, playbackPositionUs);
                this.audioTimestampPoller.rejectTimestamp();
            } else if (Math.abs(framesToDurationUs(audioTimestampPositionFrames) - playbackPositionUs) > 5000000) {
                this.listener.onPositionFramesMismatch(audioTimestampPositionFrames, audioTimestampSystemTimeUs, systemTimeUs, playbackPositionUs);
                this.audioTimestampPoller.rejectTimestamp();
            } else {
                this.audioTimestampPoller.acceptTimestamp();
            }
        }
    }

    private void maybeUpdateLatency(long systemTimeUs) {
        if (this.isOutputPcm && this.getLatencyMethod != null && systemTimeUs - this.lastLatencySampleTimeUs >= 500000) {
            try {
                this.latencyUs = (((long) ((Integer) this.getLatencyMethod.invoke(this.audioTrack, (Object[]) null)).intValue()) * 1000) - this.bufferSizeUs;
                this.latencyUs = Math.max(this.latencyUs, 0);
                if (this.latencyUs > 5000000) {
                    this.listener.onInvalidLatency(this.latencyUs);
                    this.latencyUs = 0;
                }
            } catch (Exception e) {
                this.getLatencyMethod = null;
            }
            this.lastLatencySampleTimeUs = systemTimeUs;
        }
    }

    private long framesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / ((long) this.outputSampleRate);
    }

    private void resetSyncParams() {
        this.smoothedPlayheadOffsetUs = 0;
        this.playheadOffsetCount = 0;
        this.nextPlayheadOffsetIndex = 0;
        this.lastPlayheadSampleTimeUs = 0;
    }

    private boolean forceHasPendingData() {
        return this.needsPassthroughWorkarounds && this.audioTrack.getPlayState() == 2 && getPlaybackHeadPosition() == 0;
    }

    private static boolean needsPassthroughWorkarounds(int outputEncoding) {
        return Util.SDK_INT < 23 && (outputEncoding == 5 || outputEncoding == 6);
    }

    private long getPlaybackHeadPositionUs() {
        return framesToDurationUs(getPlaybackHeadPosition());
    }

    private long getPlaybackHeadPosition() {
        if (this.stopTimestampUs != C0559C.TIME_UNSET) {
            return Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + ((((long) this.outputSampleRate) * ((SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs)) / 1000000));
        }
        int state = this.audioTrack.getPlayState();
        if (state == 1) {
            return 0;
        }
        long rawPlaybackHeadPosition = 4294967295L & ((long) this.audioTrack.getPlaybackHeadPosition());
        if (this.needsPassthroughWorkarounds) {
            if (state == 2 && rawPlaybackHeadPosition == 0) {
                this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
            }
            rawPlaybackHeadPosition += this.passthroughWorkaroundPauseOffset;
        }
        if (Util.SDK_INT <= 28) {
            if (rawPlaybackHeadPosition == 0 && this.lastRawPlaybackHeadPosition > 0 && state == 3) {
                if (this.forceResetWorkaroundTimeMs == C0559C.TIME_UNSET) {
                    this.forceResetWorkaroundTimeMs = SystemClock.elapsedRealtime();
                }
                return this.lastRawPlaybackHeadPosition;
            }
            this.forceResetWorkaroundTimeMs = C0559C.TIME_UNSET;
        }
        if (this.lastRawPlaybackHeadPosition > rawPlaybackHeadPosition) {
            this.rawPlaybackHeadWrapCount++;
        }
        this.lastRawPlaybackHeadPosition = rawPlaybackHeadPosition;
        return (this.rawPlaybackHeadWrapCount << 32) + rawPlaybackHeadPosition;
    }
}
