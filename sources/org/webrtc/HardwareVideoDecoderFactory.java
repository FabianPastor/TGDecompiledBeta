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
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
            if (r4.equals("video/x-vnd.on2.vp9") != false) goto L_0x004f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean test(android.media.MediaCodecInfo r9) {
            /*
                r8 = this;
                boolean r0 = org.webrtc.MediaCodecUtils.isHardwareAccelerated(r9)
                r1 = 0
                if (r0 != 0) goto L_0x0008
                return r1
            L_0x0008:
                java.lang.String[] r0 = r9.getSupportedTypes()
                if (r0 == 0) goto L_0x0080
                int r2 = r0.length
                if (r2 != 0) goto L_0x0013
                goto L_0x0080
            L_0x0013:
                org.telegram.messenger.voip.Instance$ServerConfig r2 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()
                r3 = 0
            L_0x0018:
                int r4 = r0.length
                r5 = 1
                if (r3 >= r4) goto L_0x007f
                r4 = r0[r3]
                r6 = -1
                int r7 = r4.hashCode()
                switch(r7) {
                    case -1662541442: goto L_0x0044;
                    case 1331836730: goto L_0x003a;
                    case 1599127256: goto L_0x0030;
                    case 1599127257: goto L_0x0027;
                    default: goto L_0x0026;
                }
            L_0x0026:
                goto L_0x004e
            L_0x0027:
                java.lang.String r7 = "video/x-vnd.on2.vp9"
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0026
                goto L_0x004f
            L_0x0030:
                java.lang.String r5 = "video/x-vnd.on2.vp8"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0026
                r5 = 0
                goto L_0x004f
            L_0x003a:
                java.lang.String r5 = "video/avc"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0026
                r5 = 2
                goto L_0x004f
            L_0x0044:
                java.lang.String r5 = "video/hevc"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0026
                r5 = 3
                goto L_0x004f
            L_0x004e:
                r5 = -1
            L_0x004f:
                switch(r5) {
                    case 0: goto L_0x006d;
                    case 1: goto L_0x006a;
                    case 2: goto L_0x0058;
                    case 3: goto L_0x0055;
                    default: goto L_0x0052;
                }
            L_0x0052:
                int r3 = r3 + 1
                goto L_0x0018
            L_0x0055:
                boolean r1 = r2.enable_h265_decoder
                return r1
            L_0x0058:
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r4 == 0) goto L_0x0067
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r4 = r4.groupCall
                if (r4 == 0) goto L_0x0067
                return r1
            L_0x0067:
                boolean r1 = r2.enable_h264_decoder
                return r1
            L_0x006a:
                boolean r1 = r2.enable_vp9_decoder
                return r1
            L_0x006d:
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r4 == 0) goto L_0x007c
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r4 = r4.groupCall
                if (r4 == 0) goto L_0x007c
                return r1
            L_0x007c:
                boolean r1 = r2.enable_vp8_decoder
                return r1
            L_0x007f:
                return r5
            L_0x0080:
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

    public HardwareVideoDecoderFactory(EglBase.Context sharedContext) {
        this(sharedContext, (Predicate<MediaCodecInfo>) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HardwareVideoDecoderFactory(org.webrtc.EglBase.Context r2, org.webrtc.Predicate<android.media.MediaCodecInfo> r3) {
        /*
            r1 = this;
            if (r3 != 0) goto L_0x0006
            org.webrtc.Predicate<android.media.MediaCodecInfo> r0 = defaultAllowedPredicate
            goto L_0x000c
        L_0x0006:
            org.webrtc.Predicate<android.media.MediaCodecInfo> r0 = defaultAllowedPredicate
            org.webrtc.Predicate r0 = r3.and(r0)
        L_0x000c:
            r1.<init>(r2, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoDecoderFactory.<init>(org.webrtc.EglBase$Context, org.webrtc.Predicate):void");
    }
}
