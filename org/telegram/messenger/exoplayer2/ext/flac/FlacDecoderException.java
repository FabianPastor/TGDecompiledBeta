package org.telegram.messenger.exoplayer2.ext.flac;

import org.telegram.messenger.exoplayer2.audio.AudioDecoderException;

public final class FlacDecoderException extends AudioDecoderException {
    FlacDecoderException(String message) {
        super(message);
    }

    FlacDecoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
