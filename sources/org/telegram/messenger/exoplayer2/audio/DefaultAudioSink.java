package org.telegram.messenger.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioFormat;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.audio.AudioSink.ConfigurationException;
import org.telegram.messenger.exoplayer2.audio.AudioSink.InitializationException;
import org.telegram.messenger.exoplayer2.audio.AudioSink.Listener;
import org.telegram.messenger.exoplayer2.audio.AudioSink.WriteException;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultAudioSink implements AudioSink {
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
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
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final String TAG = "AudioTrack";
    @SuppressLint({"InlinedApi"})
    private static final int WRITE_NON_BLOCKING = 1;
    public static boolean enablePreV21AudioSessionWorkaround = false;
    public static boolean failOnSpuriousAudioTimestamp = false;
    private AudioAttributes audioAttributes;
    private final AudioCapabilities audioCapabilities;
    private AudioProcessor[] audioProcessors;
    private int audioSessionId;
    private boolean audioTimestampSet;
    private AudioTrack audioTrack;
    private final AudioTrackUtil audioTrackUtil;
    private ByteBuffer avSyncHeader;
    private int bufferSize;
    private long bufferSizeUs;
    private int bytesUntilNextAvSync;
    private boolean canApplyPlaybackParameters;
    private int channelConfig;
    private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
    private int drainingAudioProcessorIndex;
    private PlaybackParameters drainingPlaybackParameters;
    private final boolean enableConvertHighResIntPcmToFloat;
    private int framesPerEncodedSample;
    private Method getLatencyMethod;
    private boolean handledEndOfStream;
    private boolean hasData;
    private ByteBuffer inputBuffer;
    private int inputSampleRate;
    private boolean isInputPcm;
    private AudioTrack keepSessionIdAudioTrack;
    private long lastFeedElapsedRealtimeMs;
    private long lastPlayheadSampleTimeUs;
    private long lastTimestampSampleTimeUs;
    private long latencyUs;
    private Listener listener;
    private int nextPlayheadOffsetIndex;
    private ByteBuffer outputBuffer;
    private ByteBuffer[] outputBuffers;
    private int outputEncoding;
    private int outputPcmFrameSize;
    private int pcmFrameSize;
    private PlaybackParameters playbackParameters;
    private final ArrayDeque<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
    private long playbackParametersOffsetUs;
    private long playbackParametersPositionUs;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private boolean playing;
    private byte[] preV21OutputBuffer;
    private int preV21OutputBufferOffset;
    private boolean processingEnabled;
    private final ConditionVariable releasingConditionVariable;
    private long resumeSystemTimeUs;
    private int sampleRate;
    private boolean shouldConvertHighResIntPcmToFloat;
    private long smoothedPlayheadOffsetUs;
    private final SonicAudioProcessor sonicAudioProcessor;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private final AudioProcessor[] toFloatPcmAvailableAudioProcessors;
    private final AudioProcessor[] toIntPcmAvailableAudioProcessors;
    private final TrimmingAudioProcessor trimmingAudioProcessor;
    private boolean tunneling;
    private float volume;
    private long writtenEncodedFrames;
    private long writtenPcmBytes;

    private static class AudioTrackUtil {
        private static final long FORCE_RESET_WORKAROUND_TIMEOUT_MS = 200;
        protected AudioTrack audioTrack;
        private long endPlaybackHeadPosition;
        private long forceResetWorkaroundTimeMs;
        private long lastRawPlaybackHeadPosition;
        private boolean needsPassthroughWorkaround;
        private long passthroughWorkaroundPauseOffset;
        private long rawPlaybackHeadWrapCount;
        private int sampleRate;
        private long stopPlaybackHeadPosition;
        private long stopTimestampUs;

        public boolean updateTimestamp() {
            return false;
        }

        private AudioTrackUtil() {
        }

        public void reconfigure(AudioTrack audioTrack, boolean z) {
            this.audioTrack = audioTrack;
            this.needsPassthroughWorkaround = z;
            this.stopTimestampUs = C0542C.TIME_UNSET;
            this.forceResetWorkaroundTimeMs = C0542C.TIME_UNSET;
            this.lastRawPlaybackHeadPosition = 0;
            this.rawPlaybackHeadWrapCount = 0;
            this.passthroughWorkaroundPauseOffset = 0;
            if (audioTrack != null) {
                this.sampleRate = audioTrack.getSampleRate();
            }
        }

        public void handleEndOfStream(long j) {
            this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
            this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
            this.endPlaybackHeadPosition = j;
            this.audioTrack.stop();
        }

        public void pause() {
            if (this.stopTimestampUs == C0542C.TIME_UNSET) {
                this.audioTrack.pause();
            }
        }

        public boolean needsReset(long j) {
            return (this.forceResetWorkaroundTimeMs == C0542C.TIME_UNSET || j <= 0 || SystemClock.elapsedRealtime() - this.forceResetWorkaroundTimeMs < FORCE_RESET_WORKAROUND_TIMEOUT_MS) ? 0 : 1;
        }

        public long getPlaybackHeadPosition() {
            if (this.stopTimestampUs != C0542C.TIME_UNSET) {
                return Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + ((((SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs) * ((long) this.sampleRate)) / C0542C.MICROS_PER_SECOND));
            }
            int playState = this.audioTrack.getPlayState();
            if (playState == 1) {
                return 0;
            }
            long j;
            long playbackHeadPosition = 4294967295L & ((long) this.audioTrack.getPlaybackHeadPosition());
            if (this.needsPassthroughWorkaround) {
                if (playState == 2 && playbackHeadPosition == 0) {
                    this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
                }
                j = playbackHeadPosition + this.passthroughWorkaroundPauseOffset;
            } else {
                j = playbackHeadPosition;
            }
            if (Util.SDK_INT <= 26) {
                if (j == 0 && this.lastRawPlaybackHeadPosition > 0 && playState == 3) {
                    if (this.forceResetWorkaroundTimeMs == C0542C.TIME_UNSET) {
                        this.forceResetWorkaroundTimeMs = SystemClock.elapsedRealtime();
                    }
                    return this.lastRawPlaybackHeadPosition;
                }
                this.forceResetWorkaroundTimeMs = C0542C.TIME_UNSET;
            }
            if (this.lastRawPlaybackHeadPosition > j) {
                this.rawPlaybackHeadWrapCount++;
            }
            this.lastRawPlaybackHeadPosition = j;
            return j + (this.rawPlaybackHeadWrapCount << 32);
        }

        public long getPositionUs() {
            return (getPlaybackHeadPosition() * C0542C.MICROS_PER_SECOND) / ((long) this.sampleRate);
        }

        public long getTimestampNanoTime() {
            throw new UnsupportedOperationException();
        }

        public long getTimestampFramePosition() {
            throw new UnsupportedOperationException();
        }
    }

    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        public InvalidAudioTrackTimestampException(String str) {
            super(str);
        }
    }

    private static final class PlaybackParametersCheckpoint {
        private final long mediaTimeUs;
        private final PlaybackParameters playbackParameters;
        private final long positionUs;

        private PlaybackParametersCheckpoint(PlaybackParameters playbackParameters, long j, long j2) {
            this.playbackParameters = playbackParameters;
            this.mediaTimeUs = j;
            this.positionUs = j2;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface StartMediaTimeState {
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

        public void reconfigure(AudioTrack audioTrack, boolean z) {
            super.reconfigure(audioTrack, z);
            this.rawTimestampFramePositionWrapCount = 0;
            this.lastRawTimestampFramePosition = 0;
            this.lastTimestampFramePosition = 0;
        }

        public boolean updateTimestamp() {
            boolean timestamp = this.audioTrack.getTimestamp(this.audioTimestamp);
            if (timestamp) {
                long j = this.audioTimestamp.framePosition;
                if (this.lastRawTimestampFramePosition > j) {
                    this.rawTimestampFramePositionWrapCount++;
                }
                this.lastRawTimestampFramePosition = j;
                this.lastTimestampFramePosition = j + (this.rawTimestampFramePositionWrapCount << 32);
            }
            return timestamp;
        }

        public long getTimestampNanoTime() {
            return this.audioTimestamp.nanoTime;
        }

        public long getTimestampFramePosition() {
            return this.lastTimestampFramePosition;
        }
    }

    private static boolean isEncodingPcm(int i) {
        if (!(i == 3 || i == 2 || i == Integer.MIN_VALUE || i == NUM)) {
            if (i != 4) {
                return false;
            }
        }
        return true;
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities, AudioProcessor[] audioProcessorArr) {
        this(audioCapabilities, audioProcessorArr, false);
    }

    public DefaultAudioSink(org.telegram.messenger.exoplayer2.audio.AudioCapabilities r5, org.telegram.messenger.exoplayer2.audio.AudioProcessor[] r6, boolean r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r4 = this;
        r4.<init>();
        r4.audioCapabilities = r5;
        r4.enableConvertHighResIntPcmToFloat = r7;
        r5 = new android.os.ConditionVariable;
        r7 = 1;
        r5.<init>(r7);
        r4.releasingConditionVariable = r5;
        r5 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r0 = 0;
        r1 = 18;
        if (r5 < r1) goto L_0x0023;
    L_0x0016:
        r5 = android.media.AudioTrack.class;	 Catch:{ NoSuchMethodException -> 0x0023 }
        r1 = "getLatency";	 Catch:{ NoSuchMethodException -> 0x0023 }
        r2 = r0;	 Catch:{ NoSuchMethodException -> 0x0023 }
        r2 = (java.lang.Class[]) r2;	 Catch:{ NoSuchMethodException -> 0x0023 }
        r5 = r5.getMethod(r1, r2);	 Catch:{ NoSuchMethodException -> 0x0023 }
        r4.getLatencyMethod = r5;	 Catch:{ NoSuchMethodException -> 0x0023 }
    L_0x0023:
        r5 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 19;
        if (r5 < r1) goto L_0x0031;
    L_0x0029:
        r5 = new org.telegram.messenger.exoplayer2.audio.DefaultAudioSink$AudioTrackUtilV19;
        r5.<init>();
        r4.audioTrackUtil = r5;
        goto L_0x0038;
    L_0x0031:
        r5 = new org.telegram.messenger.exoplayer2.audio.DefaultAudioSink$AudioTrackUtil;
        r5.<init>();
        r4.audioTrackUtil = r5;
    L_0x0038:
        r5 = new org.telegram.messenger.exoplayer2.audio.ChannelMappingAudioProcessor;
        r5.<init>();
        r4.channelMappingAudioProcessor = r5;
        r5 = new org.telegram.messenger.exoplayer2.audio.TrimmingAudioProcessor;
        r5.<init>();
        r4.trimmingAudioProcessor = r5;
        r5 = new org.telegram.messenger.exoplayer2.audio.SonicAudioProcessor;
        r5.<init>();
        r4.sonicAudioProcessor = r5;
        r5 = 4;
        r0 = 3;
        r1 = 0;
        r2 = r6.length;
        r5 = r5 + r2;
        r5 = new org.telegram.messenger.exoplayer2.audio.AudioProcessor[r5];
        r4.toIntPcmAvailableAudioProcessors = r5;
        r5 = r4.toIntPcmAvailableAudioProcessors;
        r2 = new org.telegram.messenger.exoplayer2.audio.ResamplingAudioProcessor;
        r2.<init>();
        r5[r1] = r2;
        r5 = r4.toIntPcmAvailableAudioProcessors;
        r2 = r4.channelMappingAudioProcessor;
        r5[r7] = r2;
        r5 = r4.toIntPcmAvailableAudioProcessors;
        r2 = 2;
        r3 = r4.trimmingAudioProcessor;
        r5[r2] = r3;
        r5 = r4.toIntPcmAvailableAudioProcessors;
        r2 = r6.length;
        java.lang.System.arraycopy(r6, r1, r5, r0, r2);
        r5 = r4.toIntPcmAvailableAudioProcessors;
        r6 = r6.length;
        r0 = r0 + r6;
        r6 = r4.sonicAudioProcessor;
        r5[r0] = r6;
        r5 = new org.telegram.messenger.exoplayer2.audio.AudioProcessor[r7];
        r6 = new org.telegram.messenger.exoplayer2.audio.FloatResamplingAudioProcessor;
        r6.<init>();
        r5[r1] = r6;
        r4.toFloatPcmAvailableAudioProcessors = r5;
        r5 = 10;
        r5 = new long[r5];
        r4.playheadOffsets = r5;
        r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4.volume = r5;
        r4.startMediaTimeState = r1;
        r5 = org.telegram.messenger.exoplayer2.audio.AudioAttributes.DEFAULT;
        r4.audioAttributes = r5;
        r4.audioSessionId = r1;
        r5 = org.telegram.messenger.exoplayer2.PlaybackParameters.DEFAULT;
        r4.playbackParameters = r5;
        r5 = -1;
        r4.drainingAudioProcessorIndex = r5;
        r5 = new org.telegram.messenger.exoplayer2.audio.AudioProcessor[r1];
        r4.audioProcessors = r5;
        r5 = new java.nio.ByteBuffer[r1];
        r4.outputBuffers = r5;
        r5 = new java.util.ArrayDeque;
        r5.<init>();
        r4.playbackParametersCheckpoints = r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.audio.DefaultAudioSink.<init>(org.telegram.messenger.exoplayer2.audio.AudioCapabilities, org.telegram.messenger.exoplayer2.audio.AudioProcessor[], boolean):void");
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean isEncodingSupported(int i) {
        boolean z = true;
        if (isEncodingPcm(i)) {
            if (i == 4) {
                if (Util.SDK_INT < 21) {
                    z = false;
                }
            }
            return z;
        }
        if (this.audioCapabilities == null || this.audioCapabilities.supportsEncoding(i) == 0) {
            z = false;
        }
        return z;
    }

    public long getCurrentPositionUs(boolean z) {
        if (!hasCurrentPositionUs()) {
            return Long.MIN_VALUE;
        }
        if (this.audioTrack.getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long nanoTime = System.nanoTime() / 1000;
        if (this.audioTimestampSet) {
            nanoTime = framesToDurationUs(this.audioTrackUtil.getTimestampFramePosition() + durationUsToFrames(nanoTime - (this.audioTrackUtil.getTimestampNanoTime() / 1000)));
        } else {
            if (this.playheadOffsetCount == 0) {
                nanoTime = this.audioTrackUtil.getPositionUs();
            } else {
                nanoTime += this.smoothedPlayheadOffsetUs;
            }
            if (!z) {
                nanoTime -= this.latencyUs;
            }
        }
        return this.startMediaTimeUs + applySpeedup(Math.min(nanoTime, framesToDurationUs(getWrittenFrames())));
    }

    public void configure(int i, int i2, int i3, int i4, int[] iArr, int i5, int i6) throws ConfigurationException {
        this.inputSampleRate = i3;
        this.isInputPcm = isEncodingPcm(i);
        boolean z = true;
        boolean z2 = this.enableConvertHighResIntPcmToFloat && isEncodingSupported(NUM) && Util.isEncodingHighResolutionIntegerPcm(i);
        this.shouldConvertHighResIntPcmToFloat = z2;
        if (this.isInputPcm) {
            this.pcmFrameSize = Util.getPcmFrameSize(i, i2);
        }
        int i7 = 4;
        z2 = this.isInputPcm && i != 4;
        boolean z3 = z2 && !this.shouldConvertHighResIntPcmToFloat;
        this.canApplyPlaybackParameters = z3;
        if (z2) {
            this.trimmingAudioProcessor.setTrimSampleCount(i5, i6);
            this.channelMappingAudioProcessor.setChannelMap(iArr);
            iArr = getAvailableAudioProcessors();
            i5 = iArr.length;
            int i8 = i;
            i6 = i3;
            i = 0;
            i3 = i;
            while (i < i5) {
                AudioProcessor audioProcessor = iArr[i];
                try {
                    i3 |= audioProcessor.configure(i6, i2, i8);
                    if (audioProcessor.isActive()) {
                        i2 = audioProcessor.getOutputChannelCount();
                        i6 = audioProcessor.getOutputSampleRateHz();
                        i8 = audioProcessor.getOutputEncoding();
                    }
                    i++;
                } catch (Throwable e) {
                    throw new ConfigurationException(e);
                }
            }
            i = i8;
        } else {
            i6 = i3;
            i3 = 0;
        }
        iArr = 12;
        i5 = 252;
        switch (i2) {
            case 1:
                break;
            case 2:
                i7 = 12;
                break;
            case 3:
                i7 = 28;
                break;
            case 4:
                i7 = 204;
                break;
            case 5:
                i7 = 220;
                break;
            case 6:
                i7 = 252;
                break;
            case 7:
                i7 = 1276;
                break;
            case 8:
                i7 = C0542C.CHANNEL_OUT_7POINT1_SURROUND;
                break;
            default:
                i3 = new StringBuilder();
                i3.append("Unsupported channel count: ");
                i3.append(i2);
                throw new ConfigurationException(i3.toString());
        }
        if (Util.SDK_INT <= 23 && "foster".equals(Util.DEVICE) && "NVIDIA".equals(Util.MANUFACTURER)) {
            if (!(i2 == 3 || i2 == 5)) {
                if (i2 == 7) {
                    i5 = C0542C.CHANNEL_OUT_7POINT1_SURROUND;
                }
            }
            if (Util.SDK_INT <= 25 || !"fugu".equals(Util.DEVICE) || this.isInputPcm || i2 != 1) {
                iArr = i5;
            }
            if (i3 == 0 || isInitialized() == 0 || this.outputEncoding != i || this.sampleRate != i6 || this.channelConfig != iArr) {
                reset();
                this.processingEnabled = z2;
                this.sampleRate = i6;
                this.channelConfig = iArr;
                this.outputEncoding = i;
                if (this.isInputPcm != 0) {
                    this.outputPcmFrameSize = Util.getPcmFrameSize(this.outputEncoding, i2);
                }
                if (i4 != 0) {
                    this.bufferSize = i4;
                } else if (this.isInputPcm == 0) {
                    i = AudioTrack.getMinBufferSize(i6, iArr, this.outputEncoding);
                    if (i != -2) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    this.bufferSize = Util.constrainValue(i * 4, ((int) durationUsToFrames(250000)) * this.outputPcmFrameSize, (int) Math.max((long) i, durationUsToFrames(750000) * ((long) this.outputPcmFrameSize)));
                } else {
                    if (this.outputEncoding != 5) {
                        if (this.outputEncoding == 6) {
                            if (this.outputEncoding != 7) {
                                this.bufferSize = 49152;
                            } else {
                                this.bufferSize = 294912;
                            }
                        }
                    }
                    this.bufferSize = CacheDataSink.DEFAULT_BUFFER_SIZE;
                }
                this.bufferSizeUs = this.isInputPcm == 0 ? framesToDurationUs((long) (this.bufferSize / this.outputPcmFrameSize)) : C0542C.TIME_UNSET;
            }
            return;
        }
        i5 = i7;
        if (Util.SDK_INT <= 25) {
        }
        iArr = i5;
        if (i3 == 0) {
        }
        reset();
        this.processingEnabled = z2;
        this.sampleRate = i6;
        this.channelConfig = iArr;
        this.outputEncoding = i;
        if (this.isInputPcm != 0) {
            this.outputPcmFrameSize = Util.getPcmFrameSize(this.outputEncoding, i2);
        }
        if (i4 != 0) {
            this.bufferSize = i4;
        } else if (this.isInputPcm == 0) {
            if (this.outputEncoding != 5) {
                if (this.outputEncoding == 6) {
                    if (this.outputEncoding != 7) {
                        this.bufferSize = 294912;
                    } else {
                        this.bufferSize = 49152;
                    }
                }
            }
            this.bufferSize = CacheDataSink.DEFAULT_BUFFER_SIZE;
        } else {
            i = AudioTrack.getMinBufferSize(i6, iArr, this.outputEncoding);
            if (i != -2) {
                z = false;
            }
            Assertions.checkState(z);
            this.bufferSize = Util.constrainValue(i * 4, ((int) durationUsToFrames(250000)) * this.outputPcmFrameSize, (int) Math.max((long) i, durationUsToFrames(750000) * ((long) this.outputPcmFrameSize)));
        }
        if (this.isInputPcm == 0) {
        }
        this.bufferSizeUs = this.isInputPcm == 0 ? framesToDurationUs((long) (this.bufferSize / this.outputPcmFrameSize)) : C0542C.TIME_UNSET;
    }

    private void resetAudioProcessors() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (AudioProcessor audioProcessor : getAvailableAudioProcessors()) {
            if (audioProcessor.isActive()) {
                arrayList.add(audioProcessor);
            } else {
                audioProcessor.flush();
            }
        }
        int size = arrayList.size();
        this.audioProcessors = (AudioProcessor[]) arrayList.toArray(new AudioProcessor[size]);
        this.outputBuffers = new ByteBuffer[size];
        while (i < size) {
            AudioProcessor audioProcessor2 = this.audioProcessors[i];
            audioProcessor2.flush();
            this.outputBuffers[i] = audioProcessor2.getOutput();
            i++;
        }
    }

    private void initialize() throws InitializationException {
        this.releasingConditionVariable.block();
        this.audioTrack = initializeAudioTrack();
        setPlaybackParameters(this.playbackParameters);
        resetAudioProcessors();
        int audioSessionId = this.audioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround && Util.SDK_INT < 21) {
            if (!(this.keepSessionIdAudioTrack == null || audioSessionId == this.keepSessionIdAudioTrack.getAudioSessionId())) {
                releaseKeepSessionIdAudioTrack();
            }
            if (this.keepSessionIdAudioTrack == null) {
                this.keepSessionIdAudioTrack = initializeKeepSessionIdAudioTrack(audioSessionId);
            }
        }
        if (this.audioSessionId != audioSessionId) {
            this.audioSessionId = audioSessionId;
            if (this.listener != null) {
                this.listener.onAudioSessionId(audioSessionId);
            }
        }
        this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
        setVolumeInternal();
        this.hasData = false;
    }

    public void play() {
        this.playing = true;
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

    public boolean handleBuffer(ByteBuffer byteBuffer, long j) throws InitializationException, WriteException {
        boolean z;
        long inputFramesToDurationUs;
        int i;
        ByteBuffer byteBuffer2 = byteBuffer;
        long j2 = j;
        if (this.inputBuffer != null) {
            if (byteBuffer2 != r0.inputBuffer) {
                z = false;
                Assertions.checkArgument(z);
                if (!isInitialized()) {
                    initialize();
                    if (r0.playing) {
                        play();
                    }
                }
                if (needsPassthroughWorkarounds()) {
                    if (r0.audioTrack.getPlayState() != 2) {
                        r0.hasData = false;
                        return false;
                    } else if (r0.audioTrack.getPlayState() == 1 && r0.audioTrackUtil.getPlaybackHeadPosition() != 0) {
                        return false;
                    }
                }
                z = r0.hasData;
                r0.hasData = hasPendingData();
                if (!(!z || r0.hasData || r0.audioTrack.getPlayState() == 1 || r0.listener == null)) {
                    r0.listener.onUnderrun(r0.bufferSize, C0542C.usToMs(r0.bufferSizeUs), SystemClock.elapsedRealtime() - r0.lastFeedElapsedRealtimeMs);
                }
                if (r0.inputBuffer == null) {
                    if (!byteBuffer.hasRemaining()) {
                        return true;
                    }
                    if (!r0.isInputPcm && r0.framesPerEncodedSample == 0) {
                        r0.framesPerEncodedSample = getFramesPerEncodedSample(r0.outputEncoding, byteBuffer2);
                        if (r0.framesPerEncodedSample == 0) {
                            return true;
                        }
                    }
                    if (r0.drainingPlaybackParameters != null) {
                        if (!drainAudioProcessorsToEndOfStream()) {
                            return false;
                        }
                        ArrayDeque arrayDeque = r0.playbackParametersCheckpoints;
                        PlaybackParametersCheckpoint playbackParametersCheckpoint = r11;
                        PlaybackParametersCheckpoint playbackParametersCheckpoint2 = new PlaybackParametersCheckpoint(r0.drainingPlaybackParameters, Math.max(0, j2), framesToDurationUs(getWrittenFrames()));
                        arrayDeque.add(playbackParametersCheckpoint);
                        r0.drainingPlaybackParameters = null;
                        resetAudioProcessors();
                    }
                    if (r0.startMediaTimeState != 0) {
                        r0.startMediaTimeUs = Math.max(0, j2);
                        r0.startMediaTimeState = 1;
                    } else {
                        inputFramesToDurationUs = r0.startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames());
                        if (r0.startMediaTimeState == 1 || Math.abs(inputFramesToDurationUs - j2) <= 200000) {
                            i = 2;
                        } else {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Discontinuity detected [expected ");
                            stringBuilder.append(inputFramesToDurationUs);
                            stringBuilder.append(", got ");
                            stringBuilder.append(j2);
                            stringBuilder.append("]");
                            Log.e(str, stringBuilder.toString());
                            i = 2;
                            r0.startMediaTimeState = 2;
                        }
                        if (r0.startMediaTimeState == i) {
                            r0.startMediaTimeUs += j2 - inputFramesToDurationUs;
                            r0.startMediaTimeState = 1;
                            if (r0.listener != null) {
                                r0.listener.onPositionDiscontinuity();
                            }
                        }
                    }
                    if (r0.isInputPcm) {
                        r0.submittedEncodedFrames += (long) r0.framesPerEncodedSample;
                    } else {
                        r0.submittedPcmBytes += (long) byteBuffer.remaining();
                    }
                    r0.inputBuffer = byteBuffer2;
                }
                if (r0.processingEnabled) {
                    writeBuffer(r0.inputBuffer, j2);
                } else {
                    processBuffers(j2);
                }
                if (!r0.inputBuffer.hasRemaining()) {
                    r0.inputBuffer = null;
                    return true;
                } else if (r0.audioTrackUtil.needsReset(getWrittenFrames())) {
                    return false;
                } else {
                    Log.w(TAG, "Resetting stalled audio track");
                    reset();
                    return true;
                }
            }
        }
        z = true;
        Assertions.checkArgument(z);
        if (isInitialized()) {
            initialize();
            if (r0.playing) {
                play();
            }
        }
        if (needsPassthroughWorkarounds()) {
            if (r0.audioTrack.getPlayState() != 2) {
                return false;
            }
            r0.hasData = false;
            return false;
        }
        z = r0.hasData;
        r0.hasData = hasPendingData();
        r0.listener.onUnderrun(r0.bufferSize, C0542C.usToMs(r0.bufferSizeUs), SystemClock.elapsedRealtime() - r0.lastFeedElapsedRealtimeMs);
        if (r0.inputBuffer == null) {
            if (!byteBuffer.hasRemaining()) {
                return true;
            }
            r0.framesPerEncodedSample = getFramesPerEncodedSample(r0.outputEncoding, byteBuffer2);
            if (r0.framesPerEncodedSample == 0) {
                return true;
            }
            if (r0.drainingPlaybackParameters != null) {
                if (!drainAudioProcessorsToEndOfStream()) {
                    return false;
                }
                ArrayDeque arrayDeque2 = r0.playbackParametersCheckpoints;
                PlaybackParametersCheckpoint playbackParametersCheckpoint3 = playbackParametersCheckpoint2;
                PlaybackParametersCheckpoint playbackParametersCheckpoint22 = new PlaybackParametersCheckpoint(r0.drainingPlaybackParameters, Math.max(0, j2), framesToDurationUs(getWrittenFrames()));
                arrayDeque2.add(playbackParametersCheckpoint3);
                r0.drainingPlaybackParameters = null;
                resetAudioProcessors();
            }
            if (r0.startMediaTimeState != 0) {
                inputFramesToDurationUs = r0.startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames());
                if (r0.startMediaTimeState == 1) {
                }
                i = 2;
                if (r0.startMediaTimeState == i) {
                    r0.startMediaTimeUs += j2 - inputFramesToDurationUs;
                    r0.startMediaTimeState = 1;
                    if (r0.listener != null) {
                        r0.listener.onPositionDiscontinuity();
                    }
                }
            } else {
                r0.startMediaTimeUs = Math.max(0, j2);
                r0.startMediaTimeState = 1;
            }
            if (r0.isInputPcm) {
                r0.submittedEncodedFrames += (long) r0.framesPerEncodedSample;
            } else {
                r0.submittedPcmBytes += (long) byteBuffer.remaining();
            }
            r0.inputBuffer = byteBuffer2;
        }
        if (r0.processingEnabled) {
            writeBuffer(r0.inputBuffer, j2);
        } else {
            processBuffers(j2);
        }
        if (!r0.inputBuffer.hasRemaining()) {
            r0.inputBuffer = null;
            return true;
        } else if (r0.audioTrackUtil.needsReset(getWrittenFrames())) {
            return false;
        } else {
            Log.w(TAG, "Resetting stalled audio track");
            reset();
            return true;
        }
    }

    private void processBuffers(long j) throws WriteException {
        int length = this.audioProcessors.length;
        int i = length;
        while (i >= 0) {
            ByteBuffer byteBuffer = i > 0 ? this.outputBuffers[i - 1] : this.inputBuffer != null ? this.inputBuffer : AudioProcessor.EMPTY_BUFFER;
            if (i == length) {
                writeBuffer(byteBuffer, j);
            } else {
                AudioProcessor audioProcessor = this.audioProcessors[i];
                audioProcessor.queueInput(byteBuffer);
                ByteBuffer output = audioProcessor.getOutput();
                this.outputBuffers[i] = output;
                if (output.hasRemaining()) {
                    i++;
                }
            }
            if (!byteBuffer.hasRemaining()) {
                i--;
            } else {
                return;
            }
        }
    }

    private void writeBuffer(ByteBuffer byteBuffer, long j) throws WriteException {
        if (byteBuffer.hasRemaining()) {
            int remaining;
            boolean z = true;
            int i = 0;
            if (this.outputBuffer != null) {
                Assertions.checkArgument(this.outputBuffer == byteBuffer);
            } else {
                this.outputBuffer = byteBuffer;
                if (Util.SDK_INT < 21) {
                    remaining = byteBuffer.remaining();
                    if (this.preV21OutputBuffer == null || this.preV21OutputBuffer.length < remaining) {
                        this.preV21OutputBuffer = new byte[remaining];
                    }
                    int position = byteBuffer.position();
                    byteBuffer.get(this.preV21OutputBuffer, 0, remaining);
                    byteBuffer.position(position);
                    this.preV21OutputBufferOffset = 0;
                }
            }
            remaining = byteBuffer.remaining();
            if (Util.SDK_INT < 21) {
                int playbackHeadPosition = this.bufferSize - ((int) (this.writtenPcmBytes - (this.audioTrackUtil.getPlaybackHeadPosition() * ((long) this.outputPcmFrameSize))));
                if (playbackHeadPosition > 0) {
                    i = this.audioTrack.write(this.preV21OutputBuffer, this.preV21OutputBufferOffset, Math.min(remaining, playbackHeadPosition));
                    if (i > 0) {
                        this.preV21OutputBufferOffset += i;
                        byteBuffer.position(byteBuffer.position() + i);
                    }
                }
            } else if (this.tunneling) {
                if (j == C0542C.TIME_UNSET) {
                    z = false;
                }
                Assertions.checkState(z);
                i = writeNonBlockingWithAvSyncV21(this.audioTrack, byteBuffer, remaining, j);
            } else {
                i = writeNonBlockingV21(this.audioTrack, byteBuffer, remaining);
            }
            this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
            if (i < 0) {
                throw new WriteException(i);
            }
            if (this.isInputPcm != null) {
                this.writtenPcmBytes += (long) i;
            }
            if (i == remaining) {
                if (this.isInputPcm == null) {
                    this.writtenEncodedFrames += (long) this.framesPerEncodedSample;
                }
                this.outputBuffer = null;
            }
        }
    }

    public void playToEndOfStream() throws WriteException {
        if (!this.handledEndOfStream) {
            if (isInitialized()) {
                if (drainAudioProcessorsToEndOfStream()) {
                    this.audioTrackUtil.handleEndOfStream(getWrittenFrames());
                    this.bytesUntilNextAvSync = 0;
                    this.handledEndOfStream = true;
                }
            }
        }
    }

    private boolean drainAudioProcessorsToEndOfStream() throws org.telegram.messenger.exoplayer2.audio.AudioSink.WriteException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:37)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:61)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = this;
        r0 = r8.drainingAudioProcessorIndex;
        r1 = -1;
        r2 = 1;
        r3 = 0;
        if (r0 != r1) goto L_0x0014;
    L_0x0007:
        r0 = r8.processingEnabled;
        if (r0 == 0) goto L_0x000d;
    L_0x000b:
        r0 = r3;
        goto L_0x0010;
    L_0x000d:
        r0 = r8.audioProcessors;
        r0 = r0.length;
    L_0x0010:
        r8.drainingAudioProcessorIndex = r0;
    L_0x0012:
        r0 = r2;
        goto L_0x0015;
    L_0x0014:
        r0 = r3;
    L_0x0015:
        r4 = r8.drainingAudioProcessorIndex;
        r5 = r8.audioProcessors;
        r6 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r5 = r5.length;
        if (r4 >= r5) goto L_0x003c;
    L_0x0021:
        r4 = r8.audioProcessors;
        r5 = r8.drainingAudioProcessorIndex;
        r4 = r4[r5];
        if (r0 == 0) goto L_0x002c;
    L_0x0029:
        r4.queueEndOfStream();
    L_0x002c:
        r8.processBuffers(r6);
        r0 = r4.isEnded();
        if (r0 != 0) goto L_0x0036;
    L_0x0035:
        return r3;
    L_0x0036:
        r0 = r8.drainingAudioProcessorIndex;
        r0 = r0 + r2;
        r8.drainingAudioProcessorIndex = r0;
        goto L_0x0012;
    L_0x003c:
        r0 = r8.outputBuffer;
        if (r0 == 0) goto L_0x004a;
    L_0x0040:
        r0 = r8.outputBuffer;
        r8.writeBuffer(r0, r6);
        r0 = r8.outputBuffer;
        if (r0 == 0) goto L_0x004a;
    L_0x0049:
        return r3;
    L_0x004a:
        r8.drainingAudioProcessorIndex = r1;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.audio.DefaultAudioSink.drainAudioProcessorsToEndOfStream():boolean");
    }

    public boolean isEnded() {
        if (isInitialized()) {
            if (!this.handledEndOfStream || hasPendingData()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPendingData() {
        return isInitialized() && (getWrittenFrames() > this.audioTrackUtil.getPlaybackHeadPosition() || overrideHasPendingData());
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        if (!isInitialized() || this.canApplyPlaybackParameters) {
            PlaybackParameters playbackParameters2 = new PlaybackParameters(this.sonicAudioProcessor.setSpeed(playbackParameters.speed), this.sonicAudioProcessor.setPitch(playbackParameters.pitch));
            playbackParameters = this.drainingPlaybackParameters != null ? this.drainingPlaybackParameters : this.playbackParametersCheckpoints.isEmpty() == null ? ((PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.getLast()).playbackParameters : this.playbackParameters;
            if (playbackParameters2.equals(playbackParameters) == null) {
                if (isInitialized() != null) {
                    this.drainingPlaybackParameters = playbackParameters2;
                } else {
                    this.playbackParameters = playbackParameters2;
                }
            }
            return this.playbackParameters;
        }
        this.playbackParameters = PlaybackParameters.DEFAULT;
        return this.playbackParameters;
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        if (!this.audioAttributes.equals(audioAttributes)) {
            this.audioAttributes = audioAttributes;
            if (this.tunneling == null) {
                reset();
                this.audioSessionId = null;
            }
        }
    }

    public void setAudioSessionId(int i) {
        if (this.audioSessionId != i) {
            this.audioSessionId = i;
            reset();
        }
    }

    public void enableTunnelingV21(int i) {
        Assertions.checkState(Util.SDK_INT >= 21);
        if (!this.tunneling || this.audioSessionId != i) {
            this.tunneling = true;
            this.audioSessionId = i;
            reset();
        }
    }

    public void disableTunneling() {
        if (this.tunneling) {
            this.tunneling = false;
            this.audioSessionId = 0;
            reset();
        }
    }

    public void setVolume(float f) {
        if (this.volume != f) {
            this.volume = f;
            setVolumeInternal();
        }
    }

    private void setVolumeInternal() {
        if (!isInitialized()) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            setVolumeInternalV21(this.audioTrack, this.volume);
        } else {
            setVolumeInternalV3(this.audioTrack, this.volume);
        }
    }

    public void pause() {
        this.playing = false;
        if (isInitialized()) {
            resetSyncParams();
            this.audioTrackUtil.pause();
        }
    }

    public void reset() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0;
            this.submittedEncodedFrames = 0;
            this.writtenPcmBytes = 0;
            this.writtenEncodedFrames = 0;
            this.framesPerEncodedSample = 0;
            if (this.drainingPlaybackParameters != null) {
                this.playbackParameters = this.drainingPlaybackParameters;
                this.drainingPlaybackParameters = null;
            } else if (!this.playbackParametersCheckpoints.isEmpty()) {
                this.playbackParameters = ((PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.getLast()).playbackParameters;
            }
            this.playbackParametersCheckpoints.clear();
            this.playbackParametersOffsetUs = 0;
            this.playbackParametersPositionUs = 0;
            this.inputBuffer = null;
            this.outputBuffer = null;
            for (int i = 0; i < this.audioProcessors.length; i++) {
                AudioProcessor audioProcessor = this.audioProcessors[i];
                audioProcessor.flush();
                this.outputBuffers[i] = audioProcessor.getOutput();
            }
            this.handledEndOfStream = false;
            this.drainingAudioProcessorIndex = -1;
            this.avSyncHeader = null;
            this.bytesUntilNextAvSync = 0;
            this.startMediaTimeState = 0;
            this.latencyUs = 0;
            resetSyncParams();
            if (this.audioTrack.getPlayState() == 3) {
                this.audioTrack.pause();
            }
            final AudioTrack audioTrack = this.audioTrack;
            this.audioTrack = null;
            this.audioTrackUtil.reconfigure(null, false);
            this.releasingConditionVariable.close();
            new Thread() {
                public void run() {
                    try {
                        audioTrack.flush();
                        audioTrack.release();
                    } finally {
                        DefaultAudioSink.this.releasingConditionVariable.open();
                    }
                }
            }.start();
        }
    }

    public void release() {
        reset();
        releaseKeepSessionIdAudioTrack();
        for (AudioProcessor reset : this.toIntPcmAvailableAudioProcessors) {
            reset.reset();
        }
        for (AudioProcessor reset2 : this.toFloatPcmAvailableAudioProcessors) {
            reset2.reset();
        }
        this.audioSessionId = 0;
        this.playing = false;
    }

    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack != null) {
            final AudioTrack audioTrack = this.keepSessionIdAudioTrack;
            this.keepSessionIdAudioTrack = null;
            new Thread() {
                public void run() {
                    audioTrack.release();
                }
            }.start();
        }
    }

    private boolean hasCurrentPositionUs() {
        return isInitialized() && this.startMediaTimeState != 0;
    }

    private long applySpeedup(long j) {
        while (!this.playbackParametersCheckpoints.isEmpty() && j >= ((PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.getFirst()).positionUs) {
            PlaybackParametersCheckpoint playbackParametersCheckpoint = (PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.remove();
            this.playbackParameters = playbackParametersCheckpoint.playbackParameters;
            this.playbackParametersPositionUs = playbackParametersCheckpoint.positionUs;
            this.playbackParametersOffsetUs = playbackParametersCheckpoint.mediaTimeUs - this.startMediaTimeUs;
        }
        if (this.playbackParameters.speed == 1.0f) {
            return (j + this.playbackParametersOffsetUs) - this.playbackParametersPositionUs;
        }
        if (this.playbackParametersCheckpoints.isEmpty()) {
            return this.playbackParametersOffsetUs + this.sonicAudioProcessor.scaleDurationForSpeedup(j - this.playbackParametersPositionUs);
        }
        return this.playbackParametersOffsetUs + Util.getMediaDurationForPlayoutDuration(j - this.playbackParametersPositionUs, this.playbackParameters.speed);
    }

    private void maybeSampleSyncParams() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r18 = this;
        r0 = r18;
        r1 = r0.audioTrackUtil;
        r1 = r1.getPositionUs();
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 != 0) goto L_0x000f;
    L_0x000e:
        return;
    L_0x000f:
        r5 = java.lang.System.nanoTime();
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r7;
        r9 = r0.lastPlayheadSampleTimeUs;
        r11 = r5 - r9;
        r9 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
        r13 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        r9 = 0;
        if (r13 < 0) goto L_0x0058;
    L_0x0021:
        r10 = r0.playheadOffsets;
        r11 = r0.nextPlayheadOffsetIndex;
        r12 = r1 - r5;
        r10[r11] = r12;
        r10 = r0.nextPlayheadOffsetIndex;
        r10 = r10 + 1;
        r11 = 10;
        r10 = r10 % r11;
        r0.nextPlayheadOffsetIndex = r10;
        r10 = r0.playheadOffsetCount;
        if (r10 >= r11) goto L_0x003c;
    L_0x0036:
        r10 = r0.playheadOffsetCount;
        r10 = r10 + 1;
        r0.playheadOffsetCount = r10;
    L_0x003c:
        r0.lastPlayheadSampleTimeUs = r5;
        r0.smoothedPlayheadOffsetUs = r3;
        r10 = r9;
    L_0x0041:
        r11 = r0.playheadOffsetCount;
        if (r10 >= r11) goto L_0x0058;
    L_0x0045:
        r11 = r0.smoothedPlayheadOffsetUs;
        r13 = r0.playheadOffsets;
        r14 = r13[r10];
        r13 = r0.playheadOffsetCount;
        r3 = (long) r13;
        r14 = r14 / r3;
        r3 = r11 + r14;
        r0.smoothedPlayheadOffsetUs = r3;
        r10 = r10 + 1;
        r3 = 0;
        goto L_0x0041;
    L_0x0058:
        r3 = r18.needsPassthroughWorkarounds();
        if (r3 == 0) goto L_0x005f;
    L_0x005e:
        return;
    L_0x005f:
        r3 = r0.lastTimestampSampleTimeUs;
        r10 = r5 - r3;
        r3 = 500000; // 0x7a120 float:7.00649E-40 double:2.47033E-318;
        r12 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1));
        if (r12 < 0) goto L_0x01a2;
    L_0x006a:
        r3 = r0.audioTrackUtil;
        r3 = r3.updateTimestamp();
        r0.audioTimestampSet = r3;
        r3 = r0.audioTimestampSet;
        r10 = 5000000; // 0x4c4b40 float:7.006492E-39 double:2.470328E-317;
        if (r3 == 0) goto L_0x014d;
    L_0x0079:
        r3 = r0.audioTrackUtil;
        r3 = r3.getTimestampNanoTime();
        r3 = r3 / r7;
        r12 = r0.audioTrackUtil;
        r12 = r12.getTimestampFramePosition();
        r14 = r0.resumeSystemTimeUs;
        r16 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
        if (r16 >= 0) goto L_0x0090;
    L_0x008c:
        r0.audioTimestampSet = r9;
        goto L_0x014d;
    L_0x0090:
        r14 = r3 - r5;
        r14 = java.lang.Math.abs(r14);
        r16 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));
        if (r16 <= 0) goto L_0x00ed;
    L_0x009a:
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "Spurious audio timestamp (system clock mismatch): ";
        r14.append(r15);
        r14.append(r12);
        r12 = ", ";
        r14.append(r12);
        r14.append(r3);
        r3 = ", ";
        r14.append(r3);
        r14.append(r5);
        r3 = ", ";
        r14.append(r3);
        r14.append(r1);
        r1 = ", ";
        r14.append(r1);
        r1 = r18.getSubmittedFrames();
        r14.append(r1);
        r1 = ", ";
        r14.append(r1);
        r1 = r18.getWrittenFrames();
        r14.append(r1);
        r1 = r14.toString();
        r2 = failOnSpuriousAudioTimestamp;
        if (r2 == 0) goto L_0x00e5;
    L_0x00df:
        r2 = new org.telegram.messenger.exoplayer2.audio.DefaultAudioSink$InvalidAudioTrackTimestampException;
        r2.<init>(r1);
        throw r2;
    L_0x00e5:
        r2 = "AudioTrack";
        android.util.Log.w(r2, r1);
        r0.audioTimestampSet = r9;
        goto L_0x014d;
    L_0x00ed:
        r14 = r0.framesToDurationUs(r12);
        r7 = r14 - r1;
        r7 = java.lang.Math.abs(r7);
        r14 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r14 <= 0) goto L_0x014d;
    L_0x00fb:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Spurious audio timestamp (frame position mismatch): ";
        r7.append(r8);
        r7.append(r12);
        r8 = ", ";
        r7.append(r8);
        r7.append(r3);
        r3 = ", ";
        r7.append(r3);
        r7.append(r5);
        r3 = ", ";
        r7.append(r3);
        r7.append(r1);
        r1 = ", ";
        r7.append(r1);
        r1 = r18.getSubmittedFrames();
        r7.append(r1);
        r1 = ", ";
        r7.append(r1);
        r1 = r18.getWrittenFrames();
        r7.append(r1);
        r1 = r7.toString();
        r2 = failOnSpuriousAudioTimestamp;
        if (r2 == 0) goto L_0x0146;
    L_0x0140:
        r2 = new org.telegram.messenger.exoplayer2.audio.DefaultAudioSink$InvalidAudioTrackTimestampException;
        r2.<init>(r1);
        throw r2;
    L_0x0146:
        r2 = "AudioTrack";
        android.util.Log.w(r2, r1);
        r0.audioTimestampSet = r9;
    L_0x014d:
        r1 = r0.getLatencyMethod;
        if (r1 == 0) goto L_0x01a0;
    L_0x0151:
        r1 = r0.isInputPcm;
        if (r1 == 0) goto L_0x01a0;
    L_0x0155:
        r1 = 0;
        r2 = r0.getLatencyMethod;	 Catch:{ Exception -> 0x019e }
        r3 = r0.audioTrack;	 Catch:{ Exception -> 0x019e }
        r4 = r1;	 Catch:{ Exception -> 0x019e }
        r4 = (java.lang.Object[]) r4;	 Catch:{ Exception -> 0x019e }
        r2 = r2.invoke(r3, r4);	 Catch:{ Exception -> 0x019e }
        r2 = (java.lang.Integer) r2;	 Catch:{ Exception -> 0x019e }
        r2 = r2.intValue();	 Catch:{ Exception -> 0x019e }
        r2 = (long) r2;	 Catch:{ Exception -> 0x019e }
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x019e }
        r2 = r2 * r7;	 Catch:{ Exception -> 0x019e }
        r7 = r0.bufferSizeUs;	 Catch:{ Exception -> 0x019e }
        r12 = r2 - r7;	 Catch:{ Exception -> 0x019e }
        r0.latencyUs = r12;	 Catch:{ Exception -> 0x019e }
        r2 = r0.latencyUs;	 Catch:{ Exception -> 0x019e }
        r7 = 0;	 Catch:{ Exception -> 0x019e }
        r2 = java.lang.Math.max(r2, r7);	 Catch:{ Exception -> 0x019e }
        r0.latencyUs = r2;	 Catch:{ Exception -> 0x019e }
        r2 = r0.latencyUs;	 Catch:{ Exception -> 0x019e }
        r4 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x019e }
        if (r4 <= 0) goto L_0x01a0;	 Catch:{ Exception -> 0x019e }
    L_0x0181:
        r2 = "AudioTrack";	 Catch:{ Exception -> 0x019e }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019e }
        r3.<init>();	 Catch:{ Exception -> 0x019e }
        r4 = "Ignoring impossibly large audio latency: ";	 Catch:{ Exception -> 0x019e }
        r3.append(r4);	 Catch:{ Exception -> 0x019e }
        r7 = r0.latencyUs;	 Catch:{ Exception -> 0x019e }
        r3.append(r7);	 Catch:{ Exception -> 0x019e }
        r3 = r3.toString();	 Catch:{ Exception -> 0x019e }
        android.util.Log.w(r2, r3);	 Catch:{ Exception -> 0x019e }
        r2 = 0;	 Catch:{ Exception -> 0x019e }
        r0.latencyUs = r2;	 Catch:{ Exception -> 0x019e }
        goto L_0x01a0;
    L_0x019e:
        r0.getLatencyMethod = r1;
    L_0x01a0:
        r0.lastTimestampSampleTimeUs = r5;
    L_0x01a2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.audio.DefaultAudioSink.maybeSampleSyncParams():void");
    }

    private boolean isInitialized() {
        return this.audioTrack != null;
    }

    private long inputFramesToDurationUs(long j) {
        return (j * C0542C.MICROS_PER_SECOND) / ((long) this.inputSampleRate);
    }

    private long framesToDurationUs(long j) {
        return (j * C0542C.MICROS_PER_SECOND) / ((long) this.sampleRate);
    }

    private long durationUsToFrames(long j) {
        return (j * ((long) this.sampleRate)) / C0542C.MICROS_PER_SECOND;
    }

    private long getSubmittedFrames() {
        return this.isInputPcm ? this.submittedPcmBytes / ((long) this.pcmFrameSize) : this.submittedEncodedFrames;
    }

    private long getWrittenFrames() {
        return this.isInputPcm ? this.writtenPcmBytes / ((long) this.outputPcmFrameSize) : this.writtenEncodedFrames;
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
        return Util.SDK_INT < 23 && (this.outputEncoding == 5 || this.outputEncoding == 6);
    }

    private boolean overrideHasPendingData() {
        return needsPassthroughWorkarounds() && this.audioTrack.getPlayState() == 2 && this.audioTrack.getPlaybackHeadPosition() == 0;
    }

    private android.media.AudioTrack initializeAudioTrack() throws org.telegram.messenger.exoplayer2.audio.AudioSink.InitializationException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r0_5 android.media.AudioTrack) in PHI: PHI: (r0_7 android.media.AudioTrack) = (r0_1 android.media.AudioTrack), (r0_5 android.media.AudioTrack), (r0_6 android.media.AudioTrack) binds: {(r0_1 android.media.AudioTrack)=B:2:0x0006, (r0_5 android.media.AudioTrack)=B:5:0x0017, (r0_6 android.media.AudioTrack)=B:6:0x0027}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        r0 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x000b;
    L_0x0006:
        r0 = r9.createAudioTrackV21();
        goto L_0x0038;
    L_0x000b:
        r0 = r9.audioAttributes;
        r0 = r0.usage;
        r2 = org.telegram.messenger.exoplayer2.util.Util.getStreamTypeForAudioUsage(r0);
        r0 = r9.audioSessionId;
        if (r0 != 0) goto L_0x0027;
    L_0x0017:
        r0 = new android.media.AudioTrack;
        r3 = r9.sampleRate;
        r4 = r9.channelConfig;
        r5 = r9.outputEncoding;
        r6 = r9.bufferSize;
        r7 = 1;
        r1 = r0;
        r1.<init>(r2, r3, r4, r5, r6, r7);
        goto L_0x0038;
    L_0x0027:
        r0 = new android.media.AudioTrack;
        r3 = r9.sampleRate;
        r4 = r9.channelConfig;
        r5 = r9.outputEncoding;
        r6 = r9.bufferSize;
        r7 = 1;
        r8 = r9.audioSessionId;
        r1 = r0;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);
    L_0x0038:
        r1 = r0.getState();
        r2 = 1;
        if (r1 == r2) goto L_0x004e;
    L_0x003f:
        r0.release();	 Catch:{ Exception -> 0x0042 }
    L_0x0042:
        r0 = new org.telegram.messenger.exoplayer2.audio.AudioSink$InitializationException;
        r2 = r9.sampleRate;
        r3 = r9.channelConfig;
        r4 = r9.bufferSize;
        r0.<init>(r1, r2, r3, r4);
        throw r0;
    L_0x004e:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.audio.DefaultAudioSink.initializeAudioTrack():android.media.AudioTrack");
    }

    @TargetApi(21)
    private AudioTrack createAudioTrackV21() {
        AudioAttributes build;
        if (this.tunneling) {
            build = new Builder().setContentType(3).setFlags(16).setUsage(1).build();
        } else {
            build = this.audioAttributes.getAudioAttributesV21();
        }
        return new AudioTrack(build, new AudioFormat.Builder().setChannelMask(this.channelConfig).setEncoding(this.outputEncoding).setSampleRate(this.sampleRate).build(), this.bufferSize, 1, this.audioSessionId != 0 ? this.audioSessionId : 0);
    }

    private AudioTrack initializeKeepSessionIdAudioTrack(int i) {
        return new AudioTrack(3, 4000, 4, 2, 2, 0, i);
    }

    private AudioProcessor[] getAvailableAudioProcessors() {
        return this.shouldConvertHighResIntPcmToFloat ? this.toFloatPcmAvailableAudioProcessors : this.toIntPcmAvailableAudioProcessors;
    }

    private static int getFramesPerEncodedSample(int i, ByteBuffer byteBuffer) {
        if (i != 7) {
            if (i != 8) {
                if (i == 5) {
                    return Ac3Util.getAc3SyncframeAudioSampleCount();
                }
                if (i == 6) {
                    return Ac3Util.parseEAc3SyncframeAudioSampleCount(byteBuffer);
                }
                if (i == 14) {
                    return Ac3Util.parseTrueHdSyncframeAudioSampleCount(byteBuffer) * 8;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected audio encoding: ");
                stringBuilder.append(i);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        return DtsUtil.parseDtsAudioSampleCount(byteBuffer);
    }

    @TargetApi(21)
    private static int writeNonBlockingV21(AudioTrack audioTrack, ByteBuffer byteBuffer, int i) {
        return audioTrack.write(byteBuffer, i, 1);
    }

    @TargetApi(21)
    private int writeNonBlockingWithAvSyncV21(AudioTrack audioTrack, ByteBuffer byteBuffer, int i, long j) {
        if (this.avSyncHeader == null) {
            this.avSyncHeader = ByteBuffer.allocate(16);
            this.avSyncHeader.order(ByteOrder.BIG_ENDIAN);
            this.avSyncHeader.putInt(NUM);
        }
        if (this.bytesUntilNextAvSync == 0) {
            this.avSyncHeader.putInt(4, i);
            this.avSyncHeader.putLong(8, j * 1000);
            this.avSyncHeader.position(0);
            this.bytesUntilNextAvSync = i;
        }
        j = this.avSyncHeader.remaining();
        if (j > null) {
            int write = audioTrack.write(this.avSyncHeader, j, 1);
            if (write < 0) {
                this.bytesUntilNextAvSync = 0;
                return write;
            } else if (write < j) {
                return 0;
            }
        }
        audioTrack = writeNonBlockingV21(audioTrack, byteBuffer, i);
        if (audioTrack < null) {
            this.bytesUntilNextAvSync = 0;
            return audioTrack;
        }
        this.bytesUntilNextAvSync -= audioTrack;
        return audioTrack;
    }

    @TargetApi(21)
    private static void setVolumeInternalV21(AudioTrack audioTrack, float f) {
        audioTrack.setVolume(f);
    }

    private static void setVolumeInternalV3(AudioTrack audioTrack, float f) {
        audioTrack.setStereoVolume(f, f);
    }
}
