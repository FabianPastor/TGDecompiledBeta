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

    public final int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
        return 8;
    }

    public FfmpegAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public FfmpegAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        this(handler, audioRendererEventListener, new DefaultAudioSink(null, audioProcessorArr), null);
    }

    public FfmpegAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioSink audioSink, boolean z) {
        super(handler, audioRendererEventListener, null, false, audioSink);
        this.enableFloatOutput = z;
    }

    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        String str = format.sampleMimeType;
        if (!MimeTypes.isAudio(str)) {
            return null;
        }
        if (FfmpegLibrary.supportsFormat(str)) {
            if (isOutputSupported(format)) {
                return BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData) == null ? 2 : 4;
            }
        }
        return 1;
    }

    protected FfmpegDecoder createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws FfmpegDecoderException {
        this.decoder = new FfmpegDecoder(16, 16, INITIAL_INPUT_BUFFER_SIZE, format.sampleMimeType, format.initializationData, shouldUseFloatOutput(format));
        return this.decoder;
    }

    public Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), this.decoder.getEncoding(), null, null, 0, null);
    }

    private boolean isOutputSupported(Format format) {
        if (shouldUseFloatOutput(format) == null) {
            if (supportsOutputEncoding(2) == null) {
                return null;
            }
        }
        return true;
    }

    private boolean shouldUseFloatOutput(Format format) {
        boolean z = false;
        if (this.enableFloatOutput) {
            if (supportsOutputEncoding(4)) {
                String str = format.sampleMimeType;
                boolean z2 = true;
                int hashCode = str.hashCode();
                if (hashCode != 187078296) {
                    if (hashCode == 187094639) {
                        if (str.equals(MimeTypes.AUDIO_RAW)) {
                            z2 = false;
                        }
                    }
                } else if (str.equals(MimeTypes.AUDIO_AC3)) {
                    z2 = true;
                }
                switch (z2) {
                    case false:
                        if (format.pcmEncoding == Integer.MIN_VALUE || format.pcmEncoding == NUM || format.pcmEncoding == 4) {
                            z = true;
                        }
                        return z;
                    case true:
                        return false;
                    default:
                        return true;
                }
            }
        }
        return false;
    }
}
