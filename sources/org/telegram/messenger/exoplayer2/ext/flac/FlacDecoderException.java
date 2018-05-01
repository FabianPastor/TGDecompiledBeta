package org.telegram.messenger.exoplayer2.ext.flac;

import org.telegram.messenger.exoplayer2.audio.AudioDecoderException;

public final class FlacDecoderException extends AudioDecoderException {
    FlacDecoderException(String str) {
        super(str);
    }

    FlacDecoderException(String str, Throwable th) {
        super(str, th);
    }
}
