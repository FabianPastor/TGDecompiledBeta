package org.webrtc;

import android.media.MediaCodecInfo;
import org.webrtc.EglBase;
import org.webrtc.Predicate;

public class HardwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
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

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0055, code lost:
            if (r3.equals("video/avc") == false) goto L_0x0037;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean test(android.media.MediaCodecInfo r8) {
            /*
                r7 = this;
                org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                r1 = 0
                if (r0 == 0) goto L_0x0010
                org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r0 = r0.groupCall
                if (r0 == 0) goto L_0x0010
                return r1
            L_0x0010:
                boolean r0 = org.webrtc.MediaCodecUtils.isHardwareAccelerated(r8)
                if (r0 != 0) goto L_0x0017
                return r1
            L_0x0017:
                java.lang.String[] r8 = r8.getSupportedTypes()
                if (r8 == 0) goto L_0x0075
                int r0 = r8.length
                if (r0 != 0) goto L_0x0021
                goto L_0x0075
            L_0x0021:
                org.telegram.messenger.voip.Instance$ServerConfig r0 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()
                r2 = 0
            L_0x0026:
                int r3 = r8.length
                r4 = 1
                if (r2 >= r3) goto L_0x0074
                r3 = r8[r2]
                r3.hashCode()
                r5 = -1
                int r6 = r3.hashCode()
                switch(r6) {
                    case -1662541442: goto L_0x0058;
                    case 1331836730: goto L_0x004f;
                    case 1599127256: goto L_0x0044;
                    case 1599127257: goto L_0x0039;
                    default: goto L_0x0037;
                }
            L_0x0037:
                r4 = -1
                goto L_0x0062
            L_0x0039:
                java.lang.String r4 = "video/x-vnd.on2.vp9"
                boolean r3 = r3.equals(r4)
                if (r3 != 0) goto L_0x0042
                goto L_0x0037
            L_0x0042:
                r4 = 3
                goto L_0x0062
            L_0x0044:
                java.lang.String r4 = "video/x-vnd.on2.vp8"
                boolean r3 = r3.equals(r4)
                if (r3 != 0) goto L_0x004d
                goto L_0x0037
            L_0x004d:
                r4 = 2
                goto L_0x0062
            L_0x004f:
                java.lang.String r6 = "video/avc"
                boolean r3 = r3.equals(r6)
                if (r3 != 0) goto L_0x0062
                goto L_0x0037
            L_0x0058:
                java.lang.String r4 = "video/hevc"
                boolean r3 = r3.equals(r4)
                if (r3 != 0) goto L_0x0061
                goto L_0x0037
            L_0x0061:
                r4 = 0
            L_0x0062:
                switch(r4) {
                    case 0: goto L_0x0071;
                    case 1: goto L_0x006e;
                    case 2: goto L_0x006b;
                    case 3: goto L_0x0068;
                    default: goto L_0x0065;
                }
            L_0x0065:
                int r2 = r2 + 1
                goto L_0x0026
            L_0x0068:
                boolean r8 = r0.enable_vp9_decoder
                return r8
            L_0x006b:
                boolean r8 = r0.enable_vp8_decoder
                return r8
            L_0x006e:
                boolean r8 = r0.enable_h264_decoder
                return r8
            L_0x0071:
                boolean r8 = r0.enable_h265_decoder
                return r8
            L_0x0074:
                return r4
            L_0x0075:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoDecoderFactory.AnonymousClass1.test(android.media.MediaCodecInfo):boolean");
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
