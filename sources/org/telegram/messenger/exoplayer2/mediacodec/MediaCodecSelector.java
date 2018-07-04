package org.telegram.messenger.exoplayer2.mediacodec;

import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT = new C06671();

    /* renamed from: org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector$1 */
    static class C06671 implements MediaCodecSelector {
        C06671() {
        }

        public MediaCodecInfo getDecoderInfo(String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
            return MediaCodecUtil.getDecoderInfo(mimeType, requiresSecureDecoder);
        }

        public MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    }

    MediaCodecInfo getDecoderInfo(String str, boolean z) throws DecoderQueryException;

    MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException;
}
