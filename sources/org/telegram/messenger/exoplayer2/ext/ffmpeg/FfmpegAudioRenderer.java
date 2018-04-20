package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.AudioSink;
import org.telegram.messenger.exoplayer2.audio.DefaultAudioSink;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class FfmpegAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int INITIAL_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private FfmpegDecoder decoder;
    private final boolean enableFloatOutput;

    public FfmpegAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public FfmpegAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        this(eventHandler, eventListener, new DefaultAudioSink(null, audioProcessors), false);
    }

    public FfmpegAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioSink audioSink, boolean enableFloatOutput) {
        super(eventHandler, eventListener, null, false, audioSink);
        this.enableFloatOutput = enableFloatOutput;
    }

    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        String sampleMimeType = format.sampleMimeType;
        if (!MimeTypes.isAudio(sampleMimeType)) {
            return 0;
        }
        if (!FfmpegLibrary.supportsFormat(sampleMimeType) || !isOutputSupported(format)) {
            return 1;
        }
        if (BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData)) {
            return 4;
        }
        return 2;
    }

    public final int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
        return 8;
    }

    protected FfmpegDecoder createDecoder(Format format, ExoMediaCrypto mediaCrypto) throws FfmpegDecoderException {
        this.decoder = new FfmpegDecoder(16, 16, INITIAL_INPUT_BUFFER_SIZE, format.sampleMimeType, format.initializationData, shouldUseFloatOutput(format));
        return this.decoder;
    }

    public Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), this.decoder.getEncoding(), null, null, 0, null);
    }

    private boolean isOutputSupported(Format inputFormat) {
        return shouldUseFloatOutput(inputFormat) || supportsOutputEncoding(2);
    }

    private boolean shouldUseFloatOutput(Format inputFormat) {
        if (!this.enableFloatOutput || !supportsOutputEncoding(4)) {
            return false;
        }
        String str = inputFormat.sampleMimeType;
        boolean z = true;
        switch (str.hashCode()) {
            case 187078296:
                if (str.equals(MimeTypes.AUDIO_AC3)) {
                    z = true;
                    break;
                }
                break;
            case 187094639:
                if (str.equals(MimeTypes.AUDIO_RAW)) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (inputFormat.pcmEncoding == Integer.MIN_VALUE || inputFormat.pcmEncoding == NUM || inputFormat.pcmEncoding == 4) {
                    return true;
                }
                return false;
            case true:
                return false;
            default:
                return true;
        }
    }
}
