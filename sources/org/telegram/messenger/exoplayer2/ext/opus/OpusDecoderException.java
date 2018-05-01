package org.telegram.messenger.exoplayer2.ext.opus;

import org.telegram.messenger.exoplayer2.audio.AudioDecoderException;

public final class OpusDecoderException extends AudioDecoderException {
    OpusDecoderException(String str) {
        super(str);
    }

    OpusDecoderException(String str, Throwable th) {
        super(str, th);
    }
}
