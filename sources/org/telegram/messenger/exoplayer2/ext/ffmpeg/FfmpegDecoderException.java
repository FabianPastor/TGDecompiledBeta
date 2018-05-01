package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import org.telegram.messenger.exoplayer2.audio.AudioDecoderException;

public final class FfmpegDecoderException extends AudioDecoderException {
    FfmpegDecoderException(String message) {
        super(message);
    }

    FfmpegDecoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
