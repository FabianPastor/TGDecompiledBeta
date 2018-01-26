package org.telegram.messenger.exoplayer2.ext.flac;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public class LibflacAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int NUM_BUFFERS = 16;

    public LibflacAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public LibflacAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        super(eventHandler, eventListener, audioProcessors);
    }

    protected int supportsFormatInternal(Format format) {
        if (MimeTypes.AUDIO_FLAC.equalsIgnoreCase(format.sampleMimeType)) {
            return 4;
        }
        return 0;
    }

    protected FlacDecoder createDecoder(Format format, ExoMediaCrypto mediaCrypto) throws FlacDecoderException {
        return new FlacDecoder(16, 16, format.initializationData);
    }
}
