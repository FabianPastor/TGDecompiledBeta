package org.webrtc;

import android.media.MediaCodecInfo;
import org.webrtc.EglBase;
import org.webrtc.Predicate;

public class HardwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
    private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>() {
        public /* synthetic */ Predicate<T> and(Predicate<? super T> predicate) {
            return Predicate.CC.$default$and(this, predicate);
        }

        public /* synthetic */ Predicate<T> negate() {
            return Predicate.CC.$default$negate(this);
        }

        public /* synthetic */ Predicate<T> or(Predicate<? super T> predicate) {
            return Predicate.CC.$default$or(this, predicate);
        }

        public boolean test(MediaCodecInfo mediaCodecInfo) {
            return MediaCodecUtils.isHardwareAccelerated(mediaCodecInfo);
        }
    };

    public /* bridge */ /* synthetic */ VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo) {
        return super.createDecoder(videoCodecInfo);
    }

    public /* bridge */ /* synthetic */ VideoCodecInfo[] getSupportedCodecs() {
        return super.getSupportedCodecs();
    }

    @Deprecated
    public HardwareVideoDecoderFactory() {
        this((EglBase.Context) null);
    }

    public HardwareVideoDecoderFactory(EglBase.Context context) {
        this(context, (Predicate<MediaCodecInfo>) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HardwareVideoDecoderFactory(org.webrtc.EglBase.Context r2, org.webrtc.Predicate<android.media.MediaCodecInfo> r3) {
        /*
            r1 = this;
            if (r3 != 0) goto L_0x0005
            org.webrtc.Predicate<android.media.MediaCodecInfo> r3 = defaultAllowedPredicate
            goto L_0x000b
        L_0x0005:
            org.webrtc.Predicate<android.media.MediaCodecInfo> r0 = defaultAllowedPredicate
            org.webrtc.Predicate r3 = r3.and(r0)
        L_0x000b:
            r1.<init>(r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoDecoderFactory.<init>(org.webrtc.EglBase$Context, org.webrtc.Predicate):void");
    }
}
