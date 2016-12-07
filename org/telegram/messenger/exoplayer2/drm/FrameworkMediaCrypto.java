package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import org.telegram.messenger.exoplayer2.util.Assertions;

@TargetApi(16)
public final class FrameworkMediaCrypto implements ExoMediaCrypto {
    private final MediaCrypto mediaCrypto;

    FrameworkMediaCrypto(MediaCrypto mediaCrypto) {
        this.mediaCrypto = (MediaCrypto) Assertions.checkNotNull(mediaCrypto);
    }

    public MediaCrypto getWrappedMediaCrypto() {
        return this.mediaCrypto;
    }

    public boolean requiresSecureDecoderComponent(String mimeType) {
        return this.mediaCrypto.requiresSecureDecoderComponent(mimeType);
    }
}
