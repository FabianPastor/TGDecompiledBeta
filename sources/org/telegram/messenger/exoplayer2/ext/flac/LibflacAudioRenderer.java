package org.telegram.messenger.exoplayer2.ext.flac;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public class LibflacAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int NUM_BUFFERS = 16;

    public LibflacAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public LibflacAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        super(handler, audioRendererEventListener, audioProcessorArr);
    }

    protected int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        if (!MimeTypes.AUDIO_FLAC.equalsIgnoreCase(format.sampleMimeType)) {
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

    protected FlacDecoder createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws FlacDecoderException {
        return new FlacDecoder(16, 16, format.initializationData);
    }
}
