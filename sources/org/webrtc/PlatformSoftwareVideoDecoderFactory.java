package org.webrtc;

import android.media.MediaCodecInfo;
import org.webrtc.EglBase;
import org.webrtc.Predicate;

public class PlatformSoftwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
    private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>() {
        public /* synthetic */ Predicate and(Predicate predicate) {
            return Predicate.CC.$default$and(this, predicate);
        }

        public /* synthetic */ Predicate negate() {
            return Predicate.CC.$default$negate(this);
        }

        public /* synthetic */ Predicate or(Predicate predicate) {
            return Predicate.CC.$default$or(this, predicate);
        }

        public boolean test(MediaCodecInfo mediaCodecInfo) {
            return MediaCodecUtils.isSoftwareOnly(mediaCodecInfo);
        }
    };

    public /* bridge */ /* synthetic */ VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo) {
        return super.createDecoder(videoCodecInfo);
    }

    public /* bridge */ /* synthetic */ VideoCodecInfo[] getSupportedCodecs() {
        return super.getSupportedCodecs();
    }

    public PlatformSoftwareVideoDecoderFactory(EglBase.Context context) {
        super(context, defaultAllowedPredicate);
    }
}
