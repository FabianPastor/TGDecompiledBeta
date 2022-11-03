package org.webrtc;

import android.media.MediaCodecInfo;
import org.webrtc.EglBase;
import org.webrtc.Predicate;
/* loaded from: classes.dex */
public class HardwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
    private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>() { // from class: org.webrtc.HardwareVideoDecoderFactory.1
        @Override // org.webrtc.Predicate
        public /* synthetic */ Predicate<MediaCodecInfo> and(Predicate<? super MediaCodecInfo> predicate) {
            return Predicate.CC.$default$and(this, predicate);
        }

        @Override // org.webrtc.Predicate
        public /* synthetic */ Predicate<MediaCodecInfo> negate() {
            return Predicate.CC.$default$negate(this);
        }

        @Override // org.webrtc.Predicate
        public /* synthetic */ Predicate<MediaCodecInfo> or(Predicate<? super MediaCodecInfo> predicate) {
            return Predicate.CC.$default$or(this, predicate);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:25:0x0047, code lost:
            if (r3.equals("video/avc") == false) goto L14;
         */
        @Override // org.webrtc.Predicate
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean test(android.media.MediaCodecInfo r8) {
            /*
                r7 = this;
                boolean r0 = org.webrtc.MediaCodecUtils.isHardwareAccelerated(r8)
                r1 = 0
                if (r0 != 0) goto L8
                return r1
            L8:
                java.lang.String[] r8 = r8.getSupportedTypes()
                if (r8 == 0) goto L85
                int r0 = r8.length
                if (r0 != 0) goto L13
                goto L85
            L13:
                org.telegram.messenger.voip.Instance$ServerConfig r0 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()
                r2 = 0
            L18:
                int r3 = r8.length
                r4 = 1
                if (r2 >= r3) goto L84
                r3 = r8[r2]
                r3.hashCode()
                r5 = -1
                int r6 = r3.hashCode()
                switch(r6) {
                    case -1662541442: goto L4a;
                    case 1331836730: goto L41;
                    case 1599127256: goto L36;
                    case 1599127257: goto L2b;
                    default: goto L29;
                }
            L29:
                r4 = -1
                goto L54
            L2b:
                java.lang.String r4 = "video/x-vnd.on2.vp9"
                boolean r3 = r3.equals(r4)
                if (r3 != 0) goto L34
                goto L29
            L34:
                r4 = 3
                goto L54
            L36:
                java.lang.String r4 = "video/x-vnd.on2.vp8"
                boolean r3 = r3.equals(r4)
                if (r3 != 0) goto L3f
                goto L29
            L3f:
                r4 = 2
                goto L54
            L41:
                java.lang.String r6 = "video/avc"
                boolean r3 = r3.equals(r6)
                if (r3 != 0) goto L54
                goto L29
            L4a:
                java.lang.String r4 = "video/hevc"
                boolean r3 = r3.equals(r4)
                if (r3 != 0) goto L53
                goto L29
            L53:
                r4 = 0
            L54:
                switch(r4) {
                    case 0: goto L81;
                    case 1: goto L6f;
                    case 2: goto L5d;
                    case 3: goto L5a;
                    default: goto L57;
                }
            L57:
                int r2 = r2 + 1
                goto L18
            L5a:
                boolean r8 = r0.enable_vp9_decoder
                return r8
            L5d:
                org.telegram.messenger.voip.VoIPService r8 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r8 == 0) goto L6c
                org.telegram.messenger.voip.VoIPService r8 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r8 = r8.groupCall
                if (r8 == 0) goto L6c
                return r1
            L6c:
                boolean r8 = r0.enable_vp8_decoder
                return r8
            L6f:
                org.telegram.messenger.voip.VoIPService r8 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r8 == 0) goto L7e
                org.telegram.messenger.voip.VoIPService r8 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r8 = r8.groupCall
                if (r8 == 0) goto L7e
                return r1
            L7e:
                boolean r8 = r0.enable_h264_decoder
                return r8
            L81:
                boolean r8 = r0.enable_h265_decoder
                return r8
            L84:
                return r4
            L85:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoDecoderFactory.AnonymousClass1.test(android.media.MediaCodecInfo):boolean");
        }
    };

    @Override // org.webrtc.MediaCodecVideoDecoderFactory, org.webrtc.VideoDecoderFactory
    public /* bridge */ /* synthetic */ VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo) {
        return super.createDecoder(videoCodecInfo);
    }

    @Override // org.webrtc.MediaCodecVideoDecoderFactory, org.webrtc.VideoDecoderFactory
    public /* bridge */ /* synthetic */ VideoCodecInfo[] getSupportedCodecs() {
        return super.getSupportedCodecs();
    }

    @Deprecated
    public HardwareVideoDecoderFactory() {
        this(null);
    }

    public HardwareVideoDecoderFactory(EglBase.Context context) {
        this(context, null);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public HardwareVideoDecoderFactory(org.webrtc.EglBase.Context r2, org.webrtc.Predicate<android.media.MediaCodecInfo> r3) {
        /*
            r1 = this;
            if (r3 != 0) goto L5
            org.webrtc.Predicate<android.media.MediaCodecInfo> r3 = org.webrtc.HardwareVideoDecoderFactory.defaultAllowedPredicate
            goto Lb
        L5:
            org.webrtc.Predicate<android.media.MediaCodecInfo> r0 = org.webrtc.HardwareVideoDecoderFactory.defaultAllowedPredicate
            org.webrtc.Predicate r3 = r3.and(r0)
        Lb:
            r1.<init>(r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoDecoderFactory.<init>(org.webrtc.EglBase$Context, org.webrtc.Predicate):void");
    }
}
