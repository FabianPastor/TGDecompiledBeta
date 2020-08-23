package org.webrtc;

import android.media.MediaCodecInfo;
import org.telegram.messenger.voip.Instance;
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
            String[] supportedTypes;
            if (!MediaCodecUtils.isHardwareAccelerated(mediaCodecInfo) || (supportedTypes = mediaCodecInfo.getSupportedTypes()) == null || supportedTypes.length == 0) {
                return false;
            }
            Instance.ServerConfig globalServerConfig = Instance.getGlobalServerConfig();
            for (String str : supportedTypes) {
                char c = 65535;
                switch (str.hashCode()) {
                    case -1662541442:
                        if (str.equals("video/hevc")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1331836730:
                        if (str.equals("video/avc")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1599127256:
                        if (str.equals("video/x-vnd.on2.vp8")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1599127257:
                        if (str.equals("video/x-vnd.on2.vp9")) {
                            c = 1;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    return globalServerConfig.enable_vp8_decoder;
                }
                if (c == 1) {
                    return globalServerConfig.enable_vp9_decoder;
                }
                if (c == 2) {
                    return globalServerConfig.enable_h264_decoder;
                }
                if (c == 3) {
                    return globalServerConfig.enable_h265_decoder;
                }
            }
            return true;
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
