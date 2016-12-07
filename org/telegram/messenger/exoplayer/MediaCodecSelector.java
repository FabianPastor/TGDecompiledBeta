package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT = new MediaCodecSelector() {
        public DecoderInfo getDecoderInfo(String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
            return MediaCodecUtil.getDecoderInfo(mimeType, requiresSecureDecoder);
        }

        public DecoderInfo getPassthroughDecoderInfo() throws DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    };

    DecoderInfo getDecoderInfo(String str, boolean z) throws DecoderQueryException;

    DecoderInfo getPassthroughDecoderInfo() throws DecoderQueryException;
}
