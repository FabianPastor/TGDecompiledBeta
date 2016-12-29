package org.telegram.messenger.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioTimestamp;
import android.media.PlaybackParams;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class AudioTrack {
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
    public static final long CURRENT_POSITION_NOT_SET = Long.MIN_VALUE;
    private static final int ERROR_BAD_VALUE = -2;
    private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000;
    private static final long MAX_BUFFER_DURATION_US = 750000;
    private static final long MAX_LATENCY_US = 5000000;
    private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
    private static final long MIN_BUFFER_DURATION_US = 250000;
    private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
    private static final int MIN_TIMESTAMP_SAMPLE_INTERVAL_US = 500000;
    private static final int MODE_STATIC = 0;
    private static final int MODE_STREAM = 1;
    private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000;
    private static final int PLAYSTATE_PAUSED = 2;
    private static final int PLAYSTATE_PLAYING = 3;
    private static final int PLAYSTATE_STOPPED = 1;
    public static final int RESULT_BUFFER_CONSUMED = 2;
    public static final int RESULT_POSITION_DISCONTINUITY = 1;
    public static final int SESSION_ID_NOT_SET = 0;
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final String TAG = "AudioTrack";
    @SuppressLint({"InlinedApi"})
    private static final int WRITE_NON_BLOCKING = 1;
    public static boolean enablePreV21AudioSessionWorkaround = false;
    public static boolean failOnSpuriousAudioTimestamp = false;
    private final AudioCapabilities audioCapabilities;
    private boolean audioTimestampSet;
    private android.media.AudioTrack audioTrack;
    private final AudioTrackUtil audioTrackUtil;
    private int bufferSize;
    private long bufferSizeUs;
    private int channelConfig;
    private ByteBuffer currentSourceBuffer;
    private int framesPerEncodedSample;
    private Method getLatencyMethod;
    private boolean hasData;
    private android.media.AudioTrack keepSessionIdAudioTrack;
    private long lastFeedElapsedRealtimeMs;
    private long lastPlayheadSampleTimeUs;
    private long lastTimestampSampleTimeUs;
    private long latencyUs;
    private final Listener listener;
    private int nextPlayheadOffsetIndex;
    private boolean passthrough;
    private int pcmFrameSize;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private final ConditionVariable releasingConditionVariable = new ConditionVariable(true);
    private ByteBuffer resampledBuffer;
    private long resumeSystemTimeUs;
    private int sampleRate;
    private long smoothedPlayheadOffsetUs;
    private int sourceEncoding;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private int streamType;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private int targetEncoding;
    private byte[] temporaryBuffer;
    private int temporaryBufferOffset;
    private boolean useResampledBuffer;
    private float volume;

    private static class AudioTrackUtil {
        protected android.media.AudioTrack audioTrack;
        private long endPlaybackHeadPosition;
        private long lastRawPlaybackHeadPosition;
        private boolean needsPassthroughWorkaround;
        private long passthroughWorkaroundPauseOffset;
        private long rawPlaybackHeadWrapCount;
        private int sampleRate;
        private long stopPlaybackHeadPosition;
        private long stopTimestampUs;

        private AudioTrackUtil() {
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean needsPassthroughWorkaround) {
            this.audioTrack = audioTrack;
            this.needsPassthroughWorkaround = needsPassthroughWorkaround;
            this.stopTimestampUs = C.TIME_UNSET;
            this.lastRawPlaybackHeadPosition = 0;
            this.rawPlaybackHeadWrapCount = 0;
            this.passthroughWorkaroundPauseOffset = 0;
            if (audioTrack != null) {
                this.sampleRate = audioTrack.getSampleRate();
            }
        }

        public void handleEndOfStream(long submittedFrames) {
            this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
            this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
            this.endPlaybackHeadPosition = submittedFrames;
            this.audioTrack.stop();
        }

        public void pause() {
            if (this.stopTimestampUs == C.TIME_UNSET) {
                this.audioTrack.pause();
            }
        }

        public long getPlaybackHeadPosition() {
            if (this.stopTimestampUs != C.TIME_UNSET) {
                return Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + ((((long) this.sampleRate) * ((SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs)) / C.MICROS_PER_SECOND));
            }
            int state = this.audioTrack.getPlayState();
            if (state == 1) {
                return 0;
            }
            long rawPlaybackHeadPosition = 4294967295L & ((long) this.audioTrack.getPlaybackHeadPosition());
            if (this.needsPassthroughWorkaround) {
                if (state == 2 && rawPlaybackHeadPosition == 0) {
                    this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
                }
                rawPlaybackHeadPosition += this.passthroughWorkaroundPauseOffset;
            }
            if (this.lastRawPlaybackHeadPosition > rawPlaybackHeadPosition) {
                this.rawPlaybackHeadWrapCount++;
            }
            this.lastRawPlaybackHeadPosition = rawPlaybackHeadPosition;
            return (this.rawPlaybackHeadWrapCount << 32) + rawPlaybackHeadPosition;
        }

        public long getPlaybackHeadPositionUs() {
            return (getPlaybackHeadPosition() * C.MICROS_PER_SECOND) / ((long) this.sampleRate);
        }

        public boolean updateTimestamp() {
            return false;
        }

        public long getTimestampNanoTime() {
            throw new UnsupportedOperationException();
        }

        public long getTimestampFramePosition() {
            throw new UnsupportedOperationException();
        }

        public void setPlaybackParams(PlaybackParams playbackParams) {
            throw new UnsupportedOperationException();
        }

        public float getPlaybackSpeed() {
            return 1.0f;
        }
    }

    public static final class InitializationException extends Exception {
        public final int audioTrackState;

        public InitializationException(int audioTrackState, int sampleRate, int channelConfig, int bufferSize) {
            super("AudioTrack init failed: " + audioTrackState + ", Config(" + sampleRate + ", " + channelConfig + ", " + bufferSize + ")");
            this.audioTrackState = audioTrackState;
        }
    }

    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        public InvalidAudioTrackTimestampException(String detailMessage) {
            super(detailMessage);
        }
    }

    public interface Listener {
        void onUnderrun(int i, long j, long j2);
    }

    public static final class WriteException extends Exception {
        public final int errorCode;

        public WriteException(int errorCode) {
            super("AudioTrack write failed: " + errorCode);
            this.errorCode = errorCode;
        }
    }

    @TargetApi(19)
    private static class AudioTrackUtilV19 extends AudioTrackUtil {
        private final AudioTimestamp audioTimestamp = new AudioTimestamp();
        private long lastRawTimestampFramePosition;
        private long lastTimestampFramePosition;
        private long rawTimestampFramePositionWrapCount;

        public AudioTrackUtilV19() {
            super();
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean needsPassthroughWorkaround) {
            super.reconfigure(audioTrack, needsPassthroughWorkaround);
            this.rawTimestampFramePositionWrapCount = 0;
            this.lastRawTimestampFramePosition = 0;
            this.lastTimestampFramePosition = 0;
        }

        public boolean updateTimestamp() {
            boolean updated = this.audioTrack.getTimestamp(this.audioTimestamp);
            if (updated) {
                long rawFramePosition = this.audioTimestamp.framePosition;
                if (this.lastRawTimestampFramePosition > rawFramePosition) {
                    this.rawTimestampFramePositionWrapCount++;
                }
                this.lastRawTimestampFramePosition = rawFramePosition;
                this.lastTimestampFramePosition = (this.rawTimestampFramePositionWrapCount << 32) + rawFramePosition;
            }
            return updated;
        }

        public long getTimestampNanoTime() {
            return this.audioTimestamp.nanoTime;
        }

        public long getTimestampFramePosition() {
            return this.lastTimestampFramePosition;
        }
    }

    @TargetApi(23)
    private static class AudioTrackUtilV23 extends AudioTrackUtilV19 {
        private PlaybackParams playbackParams;
        private float playbackSpeed = 1.0f;

        public void reconfigure(android.media.AudioTrack audioTrack, boolean needsPassthroughWorkaround) {
            super.reconfigure(audioTrack, needsPassthroughWorkaround);
            maybeApplyPlaybackParams();
        }

        public void setPlaybackParams(PlaybackParams playbackParams) {
            if (playbackParams == null) {
                playbackParams = new PlaybackParams();
            }
            playbackParams = playbackParams.allowDefaults();
            this.playbackParams = playbackParams;
            this.playbackSpeed = playbackParams.getSpeed();
            maybeApplyPlaybackParams();
        }

        public float getPlaybackSpeed() {
            return this.playbackSpeed;
        }

        private void maybeApplyPlaybackParams() {
            if (this.audioTrack != null && this.playbackParams != null) {
                this.audioTrack.setPlaybackParams(this.playbackParams);
            }
        }
    }

    public AudioTrack(AudioCapabilities audioCapabilities, Listener listener) {
        this.audioCapabilities = audioCapabilities;
        this.listener = listener;
        if (Util.SDK_INT >= 18) {
            try {
                this.getLatencyMethod = android.media.AudioTrack.class.getMethod("getLatency", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
        }
        if (Util.SDK_INT >= 23) {
            this.audioTrackUtil = new AudioTrackUtilV23();
        } else if (Util.SDK_INT >= 19) {
            this.audioTrackUtil = new AudioTrackUtilV19();
        } else {
            this.audioTrackUtil = new AudioTrackUtil();
        }
        this.playheadOffsets = new long[10];
        this.volume = 1.0f;
        this.startMediaTimeState = 0;
        this.streamType = 3;
    }

    public boolean isPassthroughSupported(String mimeType) {
        return this.audioCapabilities != null && this.audioCapabilities.supportsEncoding(getEncodingForMimeType(mimeType));
    }

    public boolean isInitialized() {
        return this.audioTrack != null;
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        if (!hasCurrentPositionUs()) {
            return Long.MIN_VALUE;
        }
        if (this.audioTrack.getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long systemClockUs = System.nanoTime() / 1000;
        if (this.audioTimestampSet) {
            return framesToDurationUs(this.audioTrackUtil.getTimestampFramePosition() + durationUsToFrames((long) (((float) (systemClockUs - (this.audioTrackUtil.getTimestampNanoTime() / 1000))) * this.audioTrackUtil.getPlaybackSpeed()))) + this.startMediaTimeUs;
        }
        long currentPositionUs;
        if (this.playheadOffsetCount == 0) {
            currentPositionUs = this.audioTrackUtil.getPlaybackHeadPositionUs() + this.startMediaTimeUs;
        } else {
            currentPositionUs = (this.smoothedPlayheadOffsetUs + systemClockUs) + this.startMediaTimeUs;
        }
        if (sourceEnded) {
            return currentPositionUs;
        }
        return currentPositionUs - this.latencyUs;
    }

    public void configure(String mimeType, int channelCount, int sampleRate, int pcmEncoding, int specifiedBufferSize) {
        int channelConfig;
        int sourceEncoding;
        switch (channelCount) {
            case 1:
                channelConfig = 4;
                break;
            case 2:
                channelConfig = 12;
                break;
            case 3:
                channelConfig = 28;
                break;
            case 4:
                channelConfig = 204;
                break;
            case 5:
                channelConfig = 220;
                break;
            case 6:
                channelConfig = 252;
                break;
            case 7:
                channelConfig = 1276;
                break;
            case 8:
                channelConfig = C.CHANNEL_OUT_7POINT1_SURROUND;
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel count: " + channelCount);
        }
        boolean passthrough = !MimeTypes.AUDIO_RAW.equals(mimeType);
        if (passthrough) {
            sourceEncoding = getEncodingForMimeType(mimeType);
        } else if (pcmEncoding == 3 || pcmEncoding == 2 || pcmEncoding == Integer.MIN_VALUE || pcmEncoding == NUM) {
            sourceEncoding = pcmEncoding;
        } else {
            throw new IllegalArgumentException("Unsupported PCM encoding: " + pcmEncoding);
        }
        if (!isInitialized() || this.sourceEncoding != sourceEncoding || this.sampleRate != sampleRate || this.channelConfig != channelConfig) {
            long j;
            reset();
            this.sourceEncoding = sourceEncoding;
            this.passthrough = passthrough;
            this.sampleRate = sampleRate;
            this.channelConfig = channelConfig;
            if (!passthrough) {
                sourceEncoding = 2;
            }
            this.targetEncoding = sourceEncoding;
            this.pcmFrameSize = channelCount * 2;
            if (specifiedBufferSize != 0) {
                this.bufferSize = specifiedBufferSize;
            } else if (!passthrough) {
                int minBufferSize = android.media.AudioTrack.getMinBufferSize(sampleRate, channelConfig, this.targetEncoding);
                Assertions.checkState(minBufferSize != -2);
                int multipliedBufferSize = minBufferSize * 4;
                int minAppBufferSize = ((int) durationUsToFrames(250000)) * this.pcmFrameSize;
                int maxAppBufferSize = (int) Math.max((long) minBufferSize, durationUsToFrames(MAX_BUFFER_DURATION_US) * ((long) this.pcmFrameSize));
                if (multipliedBufferSize >= minAppBufferSize) {
                    minAppBufferSize = multipliedBufferSize > maxAppBufferSize ? maxAppBufferSize : multipliedBufferSize;
                }
                this.bufferSize = minAppBufferSize;
            } else if (this.targetEncoding == 5 || this.targetEncoding == 6) {
                this.bufferSize = 20480;
            } else {
                this.bufferSize = 49152;
            }
            if (passthrough) {
                j = C.TIME_UNSET;
            } else {
                j = framesToDurationUs(pcmBytesToFrames((long) this.bufferSize));
            }
            this.bufferSizeUs = j;
        }
    }

    public int initialize(int sessionId) throws InitializationException {
        this.releasingConditionVariable.block();
        if (sessionId == 0) {
            this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, 1);
        } else {
            this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, 1, sessionId);
        }
        checkAudioTrackInitialized();
        sessionId = this.audioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround && Util.SDK_INT < 21) {
            if (!(this.keepSessionIdAudioTrack == null || sessionId == this.keepSessionIdAudioTrack.getAudioSessionId())) {
                releaseKeepSessionIdAudioTrack();
            }
            if (this.keepSessionIdAudioTrack == null) {
                this.keepSessionIdAudioTrack = new android.media.AudioTrack(this.streamType, 4000, 4, 2, 2, 0, sessionId);
            }
        }
        this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
        setAudioTrackVolume();
        this.hasData = false;
        return sessionId;
    }

    public void play() {
        if (isInitialized()) {
            this.resumeSystemTimeUs = System.nanoTime() / 1000;
            this.audioTrack.play();
        }
    }

    public void handleDiscontinuity() {
        if (this.startMediaTimeState == 1) {
            this.startMediaTimeState = 2;
        }
    }

    public int handleBuffer(ByteBuffer buffer, long presentationTimeUs) throws WriteException {
        boolean hadData = this.hasData;
        this.hasData = hasPendingData();
        if (!(!hadData || this.hasData || this.audioTrack.getPlayState() == 1)) {
            this.listener.onUnderrun(this.bufferSize, C.usToMs(this.bufferSizeUs), SystemClock.elapsedRealtime() - this.lastFeedElapsedRealtimeMs);
        }
        int result = writeBuffer(buffer, presentationTimeUs);
        this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
        return result;
    }

    private int writeBuffer(ByteBuffer buffer, long presentationTimeUs) throws WriteException {
        int bytesRemaining;
        boolean isNewSourceBuffer = this.currentSourceBuffer == null;
        boolean z = isNewSourceBuffer || this.currentSourceBuffer == buffer;
        Assertions.checkState(z);
        this.currentSourceBuffer = buffer;
        if (needsPassthroughWorkarounds()) {
            if (this.audioTrack.getPlayState() == 2) {
                return 0;
            }
            if (this.audioTrack.getPlayState() == 1 && this.audioTrackUtil.getPlaybackHeadPosition() != 0) {
                return 0;
            }
        }
        int result = 0;
        if (isNewSourceBuffer) {
            if (this.currentSourceBuffer.hasRemaining()) {
                this.useResampledBuffer = this.targetEncoding != this.sourceEncoding;
                if (this.useResampledBuffer) {
                    Assertions.checkState(this.targetEncoding == 2);
                    this.resampledBuffer = resampleTo16BitPcm(this.currentSourceBuffer, this.sourceEncoding, this.resampledBuffer);
                    buffer = this.resampledBuffer;
                }
                if (this.passthrough && this.framesPerEncodedSample == 0) {
                    this.framesPerEncodedSample = getFramesPerEncodedSample(this.targetEncoding, buffer);
                }
                if (this.startMediaTimeState == 0) {
                    this.startMediaTimeUs = Math.max(0, presentationTimeUs);
                    this.startMediaTimeState = 1;
                } else {
                    long expectedPresentationTimeUs = this.startMediaTimeUs + framesToDurationUs(getSubmittedFrames());
                    if (this.startMediaTimeState == 1 && Math.abs(expectedPresentationTimeUs - presentationTimeUs) > 200000) {
                        Log.e(TAG, "Discontinuity detected [expected " + expectedPresentationTimeUs + ", got " + presentationTimeUs + "]");
                        this.startMediaTimeState = 2;
                    }
                    if (this.startMediaTimeState == 2) {
                        this.startMediaTimeUs += presentationTimeUs - expectedPresentationTimeUs;
                        this.startMediaTimeState = 1;
                        result = 0 | 1;
                    }
                }
                if (Util.SDK_INT < 21) {
                    bytesRemaining = buffer.remaining();
                    if (this.temporaryBuffer == null || this.temporaryBuffer.length < bytesRemaining) {
                        this.temporaryBuffer = new byte[bytesRemaining];
                    }
                    int originalPosition = buffer.position();
                    buffer.get(this.temporaryBuffer, 0, bytesRemaining);
                    buffer.position(originalPosition);
                    this.temporaryBufferOffset = 0;
                }
            } else {
                this.currentSourceBuffer = null;
                return 2;
            }
        }
        if (this.useResampledBuffer) {
            buffer = this.resampledBuffer;
        }
        bytesRemaining = buffer.remaining();
        int bytesWritten = 0;
        if (Util.SDK_INT < 21) {
            int bytesToWrite = this.bufferSize - ((int) (this.submittedPcmBytes - (this.audioTrackUtil.getPlaybackHeadPosition() * ((long) this.pcmFrameSize))));
            if (bytesToWrite > 0) {
                bytesWritten = this.audioTrack.write(this.temporaryBuffer, this.temporaryBufferOffset, Math.min(bytesRemaining, bytesToWrite));
                if (bytesWritten >= 0) {
                    this.temporaryBufferOffset += bytesWritten;
                }
                buffer.position(buffer.position() + bytesWritten);
            }
        } else {
            bytesWritten = writeNonBlockingV21(this.audioTrack, buffer, bytesRemaining);
        }
        if (bytesWritten < 0) {
            throw new WriteException(bytesWritten);
        }
        if (!this.passthrough) {
            this.submittedPcmBytes += (long) bytesWritten;
        }
        if (bytesWritten != bytesRemaining) {
            return result;
        }
        if (this.passthrough) {
            this.submittedEncodedFrames += (long) this.framesPerEncodedSample;
        }
        this.currentSourceBuffer = null;
        return result | 2;
    }

    public void handleEndOfStream() {
        if (isInitialized()) {
            this.audioTrackUtil.handleEndOfStream(getSubmittedFrames());
        }
    }

    public boolean hasPendingData() {
        return isInitialized() && (getSubmittedFrames() > this.audioTrackUtil.getPlaybackHeadPosition() || overrideHasPendingData());
    }

    public void setPlaybackParams(PlaybackParams playbackParams) {
        this.audioTrackUtil.setPlaybackParams(playbackParams);
    }

    public boolean setStreamType(int streamType) {
        if (this.streamType == streamType) {
            return false;
        }
        this.streamType = streamType;
        reset();
        return true;
    }

    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.volume = volume;
            setAudioTrackVolume();
        }
    }

    private void setAudioTrackVolume() {
        if (!isInitialized()) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            setAudioTrackVolumeV21(this.audioTrack, this.volume);
        } else {
            setAudioTrackVolumeV3(this.audioTrack, this.volume);
        }
    }

    public void pause() {
        if (isInitialized()) {
            resetSyncParams();
            this.audioTrackUtil.pause();
        }
    }

    public void reset() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0;
            this.submittedEncodedFrames = 0;
            this.framesPerEncodedSample = 0;
            this.currentSourceBuffer = null;
            this.startMediaTimeState = 0;
            this.latencyUs = 0;
            resetSyncParams();
            if (this.audioTrack.getPlayState() == 3) {
                this.audioTrack.pause();
            }
            final android.media.AudioTrack toRelease = this.audioTrack;
            this.audioTrack = null;
            this.audioTrackUtil.reconfigure(null, false);
            this.releasingConditionVariable.close();
            new Thread() {
                public void run() {
                    try {
                        toRelease.flush();
                        toRelease.release();
                    } finally {
                        AudioTrack.this.releasingConditionVariable.open();
                    }
                }
            }.start();
        }
    }

    public void release() {
        reset();
        releaseKeepSessionIdAudioTrack();
    }

    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack != null) {
            final android.media.AudioTrack toRelease = this.keepSessionIdAudioTrack;
            this.keepSessionIdAudioTrack = null;
            new Thread() {
                public void run() {
                    toRelease.release();
                }
            }.start();
        }
    }

    private boolean hasCurrentPositionUs() {
        return isInitialized() && this.startMediaTimeState != 0;
    }

    private void maybeSampleSyncParams() {
        long playbackPositionUs = this.audioTrackUtil.getPlaybackHeadPositionUs();
        if (playbackPositionUs != 0) {
            long systemClockUs = System.nanoTime() / 1000;
            if (systemClockUs - this.lastPlayheadSampleTimeUs >= 30000) {
                this.playheadOffsets[this.nextPlayheadOffsetIndex] = playbackPositionUs - systemClockUs;
                this.nextPlayheadOffsetIndex = (this.nextPlayheadOffsetIndex + 1) % 10;
                if (this.playheadOffsetCount < 10) {
                    this.playheadOffsetCount++;
                }
                this.lastPlayheadSampleTimeUs = systemClockUs;
                this.smoothedPlayheadOffsetUs = 0;
                for (int i = 0; i < this.playheadOffsetCount; i++) {
                    this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / ((long) this.playheadOffsetCount);
                }
            }
            if (!needsPassthroughWorkarounds() && systemClockUs - this.lastTimestampSampleTimeUs >= 500000) {
                this.audioTimestampSet = this.audioTrackUtil.updateTimestamp();
                if (this.audioTimestampSet) {
                    long audioTimestampUs = this.audioTrackUtil.getTimestampNanoTime() / 1000;
                    long audioTimestampFramePosition = this.audioTrackUtil.getTimestampFramePosition();
                    if (audioTimestampUs < this.resumeSystemTimeUs) {
                        this.audioTimestampSet = false;
                    } else if (Math.abs(audioTimestampUs - systemClockUs) > 5000000) {
                        message = "Spurious audio timestamp (system clock mismatch): " + audioTimestampFramePosition + ", " + audioTimestampUs + ", " + systemClockUs + ", " + playbackPositionUs;
                        if (failOnSpuriousAudioTimestamp) {
                            throw new InvalidAudioTrackTimestampException(message);
                        }
                        Log.w(TAG, message);
                        this.audioTimestampSet = false;
                    } else if (Math.abs(framesToDurationUs(audioTimestampFramePosition) - playbackPositionUs) > 5000000) {
                        message = "Spurious audio timestamp (frame position mismatch): " + audioTimestampFramePosition + ", " + audioTimestampUs + ", " + systemClockUs + ", " + playbackPositionUs;
                        if (failOnSpuriousAudioTimestamp) {
                            throw new InvalidAudioTrackTimestampException(message);
                        }
                        Log.w(TAG, message);
                        this.audioTimestampSet = false;
                    }
                }
                if (!(this.getLatencyMethod == null || this.passthrough)) {
                    try {
                        this.latencyUs = (((long) ((Integer) this.getLatencyMethod.invoke(this.audioTrack, (Object[]) null)).intValue()) * 1000) - this.bufferSizeUs;
                        this.latencyUs = Math.max(this.latencyUs, 0);
                        if (this.latencyUs > 5000000) {
                            Log.w(TAG, "Ignoring impossibly large audio latency: " + this.latencyUs);
                            this.latencyUs = 0;
                        }
                    } catch (Exception e) {
                        this.getLatencyMethod = null;
                    }
                }
                this.lastTimestampSampleTimeUs = systemClockUs;
            }
        }
    }

    private void checkAudioTrackInitialized() throws InitializationException {
        int state = this.audioTrack.getState();
        if (state != 1) {
            try {
                this.audioTrack.release();
            } catch (Exception e) {
            } finally {
                this.audioTrack = null;
            }
            throw new InitializationException(state, this.sampleRate, this.channelConfig, this.bufferSize);
        }
    }

    private long pcmBytesToFrames(long byteCount) {
        return byteCount / ((long) this.pcmFrameSize);
    }

    private long framesToDurationUs(long frameCount) {
        return (C.MICROS_PER_SECOND * frameCount) / ((long) this.sampleRate);
    }

    private long durationUsToFrames(long durationUs) {
        return (((long) this.sampleRate) * durationUs) / C.MICROS_PER_SECOND;
    }

    private long getSubmittedFrames() {
        return this.passthrough ? this.submittedEncodedFrames : pcmBytesToFrames(this.submittedPcmBytes);
    }

    private void resetSyncParams() {
        this.smoothedPlayheadOffsetUs = 0;
        this.playheadOffsetCount = 0;
        this.nextPlayheadOffsetIndex = 0;
        this.lastPlayheadSampleTimeUs = 0;
        this.audioTimestampSet = false;
        this.lastTimestampSampleTimeUs = 0;
    }

    private boolean needsPassthroughWorkarounds() {
        return Util.SDK_INT < 23 && (this.targetEncoding == 5 || this.targetEncoding == 6);
    }

    private boolean overrideHasPendingData() {
        return needsPassthroughWorkarounds() && this.audioTrack.getPlayState() == 2 && this.audioTrack.getPlaybackHeadPosition() == 0;
    }

    private static ByteBuffer resampleTo16BitPcm(ByteBuffer buffer, int sourceEncoding, ByteBuffer out) {
        int resampledSize;
        int offset = buffer.position();
        int limit = buffer.limit();
        int size = limit - offset;
        switch (sourceEncoding) {
            case Integer.MIN_VALUE:
                resampledSize = (size / 3) * 2;
                break;
            case 3:
                resampledSize = size * 2;
                break;
            case 1073741824:
                resampledSize = size / 2;
                break;
            default:
                throw new IllegalStateException();
        }
        ByteBuffer resampledBuffer = out;
        if (resampledBuffer == null || resampledBuffer.capacity() < resampledSize) {
            resampledBuffer = ByteBuffer.allocateDirect(resampledSize);
        }
        resampledBuffer.position(0);
        resampledBuffer.limit(resampledSize);
        int i;
        switch (sourceEncoding) {
            case Integer.MIN_VALUE:
                for (i = offset; i < limit; i += 3) {
                    resampledBuffer.put(buffer.get(i + 1));
                    resampledBuffer.put(buffer.get(i + 2));
                }
                break;
            case 3:
                for (i = offset; i < limit; i++) {
                    resampledBuffer.put((byte) 0);
                    resampledBuffer.put((byte) ((buffer.get(i) & 255) - 128));
                }
                break;
            case 1073741824:
                for (i = offset; i < limit; i += 4) {
                    resampledBuffer.put(buffer.get(i + 2));
                    resampledBuffer.put(buffer.get(i + 3));
                }
                break;
            default:
                throw new IllegalStateException();
        }
        resampledBuffer.position(0);
        return resampledBuffer;
    }

    private static int getEncodingForMimeType(String mimeType) {
        int i = -1;
        switch (mimeType.hashCode()) {
            case -1095064472:
                if (mimeType.equals(MimeTypes.AUDIO_DTS)) {
                    i = 2;
                    break;
                }
                break;
            case 187078296:
                if (mimeType.equals(MimeTypes.AUDIO_AC3)) {
                    i = 0;
                    break;
                }
                break;
            case 1504578661:
                if (mimeType.equals(MimeTypes.AUDIO_E_AC3)) {
                    i = 1;
                    break;
                }
                break;
            case 1505942594:
                if (mimeType.equals(MimeTypes.AUDIO_DTS_HD)) {
                    i = 3;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                return 5;
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 8;
            default:
                return 0;
        }
    }

    private static int getFramesPerEncodedSample(int encoding, ByteBuffer buffer) {
        if (encoding == 7 || encoding == 8) {
            return DtsUtil.parseDtsAudioSampleCount(buffer);
        }
        if (encoding == 5) {
            return Ac3Util.getAc3SyncframeAudioSampleCount();
        }
        if (encoding == 6) {
            return Ac3Util.parseEAc3SyncframeAudioSampleCount(buffer);
        }
        throw new IllegalStateException("Unexpected audio encoding: " + encoding);
    }

    @TargetApi(21)
    private static int writeNonBlockingV21(android.media.AudioTrack audioTrack, ByteBuffer buffer, int size) {
        return audioTrack.write(buffer, size, 1);
    }

    @TargetApi(21)
    private static void setAudioTrackVolumeV21(android.media.AudioTrack audioTrack, float volume) {
        audioTrack.setVolume(volume);
    }

    private static void setAudioTrackVolumeV3(android.media.AudioTrack audioTrack, float volume) {
        audioTrack.setStereoVolume(volume, volume);
    }
}
