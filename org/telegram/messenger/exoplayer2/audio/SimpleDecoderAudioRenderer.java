package org.telegram.messenger.exoplayer2.audio;

import android.media.PlaybackParams;
import android.os.Handler;
import android.os.SystemClock;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.audio.AudioTrack.InitializationException;
import org.telegram.messenger.exoplayer2.audio.AudioTrack.WriteException;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TraceUtil;

public abstract class SimpleDecoderAudioRenderer extends BaseRenderer implements MediaClock {
    private boolean allowPositionDiscontinuity;
    private int audioSessionId;
    private final AudioTrack audioTrack;
    private boolean audioTrackHasData;
    private long currentPositionUs;
    private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
    private DecoderCounters decoderCounters;
    private final EventDispatcher eventDispatcher;
    private final FormatHolder formatHolder;
    private DecoderInputBuffer inputBuffer;
    private Format inputFormat;
    private boolean inputStreamEnded;
    private long lastFeedElapsedRealtimeMs;
    private SimpleOutputBuffer outputBuffer;
    private boolean outputStreamEnded;

    protected abstract SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> createDecoder(Format format) throws AudioDecoderException;

    public SimpleDecoderAudioRenderer() {
        this(null, null);
    }

    public SimpleDecoderAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener) {
        this(eventHandler, eventListener, null, 3);
    }

    public SimpleDecoderAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioCapabilities audioCapabilities, int streamType) {
        super(1);
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.audioSessionId = 0;
        this.audioTrack = new AudioTrack(audioCapabilities, streamType);
        this.formatHolder = new FormatHolder();
    }

    public MediaClock getMediaClock() {
        return this;
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        Exception e;
        if (!this.outputStreamEnded) {
            if (this.inputFormat != null || readFormat()) {
                if (this.decoder == null) {
                    try {
                        long codecInitializingTimestamp = SystemClock.elapsedRealtime();
                        TraceUtil.beginSection("createAudioDecoder");
                        this.decoder = createDecoder(this.inputFormat);
                        TraceUtil.endSection();
                        long codecInitializedTimestamp = SystemClock.elapsedRealtime();
                        this.eventDispatcher.decoderInitialized(this.decoder.getName(), codecInitializedTimestamp, codecInitializedTimestamp - codecInitializingTimestamp);
                        DecoderCounters decoderCounters = this.decoderCounters;
                        decoderCounters.decoderInitCount++;
                    } catch (AudioDecoderException e2) {
                        throw ExoPlaybackException.createForRenderer(e2, getIndex());
                    }
                }
                try {
                    TraceUtil.beginSection("drainAndFeed");
                    do {
                    } while (drainOutputBuffer());
                    do {
                    } while (feedInputBuffer());
                    TraceUtil.endSection();
                    this.decoderCounters.ensureUpdated();
                } catch (InitializationException e3) {
                    e = e3;
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                } catch (WriteException e4) {
                    e = e4;
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                } catch (AudioDecoderException e5) {
                    e = e5;
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                }
            }
        }
    }

    protected Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.inputFormat.channelCount, this.inputFormat.sampleRate, 2, null, null, 0, null);
    }

    private boolean drainOutputBuffer() throws AudioDecoderException, InitializationException, WriteException {
        if (this.outputStreamEnded) {
            return false;
        }
        if (this.outputBuffer == null) {
            this.outputBuffer = (SimpleOutputBuffer) this.decoder.dequeueOutputBuffer();
            if (this.outputBuffer == null) {
                return false;
            }
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
        }
        if (this.outputBuffer.isEndOfStream()) {
            this.outputStreamEnded = true;
            this.audioTrack.handleEndOfStream();
            this.outputBuffer.release();
            this.outputBuffer = null;
            return false;
        }
        if (this.audioTrack.isInitialized()) {
            boolean audioTrackHadData = this.audioTrackHasData;
            this.audioTrackHasData = this.audioTrack.hasPendingData();
            if (audioTrackHadData && !this.audioTrackHasData && getState() == 2) {
                long elapsedSinceLastFeedMs = SystemClock.elapsedRealtime() - this.lastFeedElapsedRealtimeMs;
                this.eventDispatcher.audioTrackUnderrun(this.audioTrack.getBufferSize(), C.usToMs(this.audioTrack.getBufferSizeUs()), elapsedSinceLastFeedMs);
            }
        } else {
            Format outputFormat = getOutputFormat();
            this.audioTrack.configure(outputFormat.sampleMimeType, outputFormat.channelCount, outputFormat.sampleRate, outputFormat.pcmEncoding, 0);
            if (this.audioSessionId == 0) {
                this.audioSessionId = this.audioTrack.initialize(0);
                this.eventDispatcher.audioSessionId(this.audioSessionId);
                onAudioSessionId(this.audioSessionId);
            } else {
                this.audioTrack.initialize(this.audioSessionId);
            }
            this.audioTrackHasData = false;
            if (getState() == 2) {
                this.audioTrack.play();
            }
        }
        int handleBufferResult = this.audioTrack.handleBuffer(this.outputBuffer.data, this.outputBuffer.timeUs);
        this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
        if ((handleBufferResult & 1) != 0) {
            this.allowPositionDiscontinuity = true;
        }
        if ((handleBufferResult & 2) == 0) {
            return false;
        }
        decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.outputBuffer.release();
        this.outputBuffer = null;
        return true;
    }

    private boolean feedInputBuffer() throws AudioDecoderException {
        if (this.inputStreamEnded) {
            return false;
        }
        if (this.inputBuffer == null) {
            this.inputBuffer = this.decoder.dequeueInputBuffer();
            if (this.inputBuffer == null) {
                return false;
            }
        }
        int result = readSource(this.formatHolder, this.inputBuffer);
        if (result == -3) {
            return false;
        }
        if (result == -5) {
            onInputFormatChanged(this.formatHolder.format);
            return true;
        } else if (this.inputBuffer.isEndOfStream()) {
            this.inputStreamEnded = true;
            this.decoder.queueInputBuffer(this.inputBuffer);
            this.inputBuffer = null;
            return false;
        } else {
            this.inputBuffer.flip();
            this.decoder.queueInputBuffer(this.inputBuffer);
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.inputBufferCount++;
            this.inputBuffer = null;
            return true;
        }
    }

    private void flushDecoder() {
        this.inputBuffer = null;
        if (this.outputBuffer != null) {
            this.outputBuffer.release();
            this.outputBuffer = null;
        }
        this.decoder.flush();
    }

    public boolean isEnded() {
        return this.outputStreamEnded && !this.audioTrack.hasPendingData();
    }

    public boolean isReady() {
        return this.audioTrack.hasPendingData() || (this.inputFormat != null && (isSourceReady() || this.outputBuffer != null));
    }

    public long getPositionUs() {
        long newCurrentPositionUs = this.audioTrack.getCurrentPositionUs(isEnded());
        if (newCurrentPositionUs != Long.MIN_VALUE) {
            if (!this.allowPositionDiscontinuity) {
                newCurrentPositionUs = Math.max(this.currentPositionUs, newCurrentPositionUs);
            }
            this.currentPositionUs = newCurrentPositionUs;
            this.allowPositionDiscontinuity = false;
        }
        return this.currentPositionUs;
    }

    protected void onAudioSessionId(int audioSessionId) {
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        this.decoderCounters = new DecoderCounters();
        this.eventDispatcher.enabled(this.decoderCounters);
    }

    protected void onPositionReset(long positionUs, boolean joining) {
        this.audioTrack.reset();
        this.currentPositionUs = positionUs;
        this.allowPositionDiscontinuity = true;
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.decoder != null) {
            flushDecoder();
        }
    }

    protected void onStarted() {
        this.audioTrack.play();
    }

    protected void onStopped() {
        this.audioTrack.pause();
    }

    protected void onDisabled() {
        this.inputBuffer = null;
        this.outputBuffer = null;
        this.inputFormat = null;
        this.audioSessionId = 0;
        try {
            if (this.decoder != null) {
                this.decoder.release();
                this.decoder = null;
                DecoderCounters decoderCounters = this.decoderCounters;
                decoderCounters.decoderReleaseCount++;
            }
            this.audioTrack.release();
        } finally {
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    private boolean readFormat() {
        if (readSource(this.formatHolder, null) != -5) {
            return false;
        }
        onInputFormatChanged(this.formatHolder.format);
        return true;
    }

    private void onInputFormatChanged(Format newFormat) {
        this.inputFormat = newFormat;
        this.eventDispatcher.inputFormatChanged(newFormat);
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        switch (messageType) {
            case 2:
                this.audioTrack.setVolume(((Float) message).floatValue());
                return;
            case 3:
                this.audioTrack.setPlaybackParams((PlaybackParams) message);
                return;
            default:
                super.handleMessage(messageType, message);
                return;
        }
    }
}
