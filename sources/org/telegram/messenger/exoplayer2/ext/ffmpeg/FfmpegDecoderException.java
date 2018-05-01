package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import org.telegram.messenger.exoplayer2.audio.AudioDecoderException;

public final class FfmpegDecoderException extends AudioDecoderException {
    FfmpegDecoderException(String str) {
        super(str);
    }

    FfmpegDecoderException(String str, Throwable th) {
        super(str, th);
    }
}
