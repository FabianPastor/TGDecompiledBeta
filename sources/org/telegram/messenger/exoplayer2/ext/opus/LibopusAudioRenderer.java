package org.telegram.messenger.exoplayer2.ext.opus;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class LibopusAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int INITIAL_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private OpusDecoder decoder;

    public LibopusAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public LibopusAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        super(handler, audioRendererEventListener, audioProcessorArr);
    }

    public LibopusAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean z, AudioProcessor... audioProcessorArr) {
        super(handler, audioRendererEventListener, null, drmSessionManager, z, audioProcessorArr);
    }

    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        if (!MimeTypes.AUDIO_OPUS.equalsIgnoreCase(format.sampleMimeType)) {
            return null;
        }
        if (!supportsOutputEncoding(2)) {
            return 1;
        }
        if (BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData) == null) {
            return 2;
        }
        return 4;
    }

    protected OpusDecoder createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws OpusDecoderException {
        this.decoder = new OpusDecoder(16, 16, INITIAL_INPUT_BUFFER_SIZE, format.initializationData, exoMediaCrypto);
        return this.decoder;
    }

    protected Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), 2, null, null, 0, null);
    }
}
