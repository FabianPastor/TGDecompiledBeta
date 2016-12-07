package org.telegram.messenger.exoplayer2.audio;

public abstract class AudioDecoderException extends Exception {
    public AudioDecoderException(String detailMessage) {
        super(detailMessage);
    }
}
