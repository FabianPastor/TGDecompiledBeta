package org.webrtc;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.FileLog;

class MediaCodecUtils {
    static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = NUM;
    static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka = NUM;
    static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka = NUM;
    static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka = NUM;
    static final int[] DECODER_COLOR_FORMATS = {19, 21, NUM, NUM, NUM, NUM, NUM};
    static final int[] ENCODER_COLOR_FORMATS = {19, 21, NUM, NUM};
    static final String EXYNOS_PREFIX = "OMX.Exynos.";
    static final String HISI_PREFIX = "OMX.hisi.";
    static final String INTEL_PREFIX = "OMX.Intel.";
    static final String NVIDIA_PREFIX = "OMX.Nvidia.";
    static final String QCOM_PREFIX = "OMX.qcom.";
    static final String[] SOFTWARE_IMPLEMENTATION_PREFIXES = {"OMX.google.", "OMX.SEC.", "c2.android"};
    private static final String TAG = "MediaCodecUtils";
    static final int[] TEXTURE_COLOR_FORMATS = getTextureColorFormats();

    private static int[] getTextureColorFormats() {
        if (Build.VERSION.SDK_INT < 18) {
            return new int[0];
        }
        return new int[]{NUM};
    }

    public static ArrayList<MediaCodecInfo> getSortedCodecsList() {
        ArrayList<MediaCodecInfo> arrayList = new ArrayList<>();
        try {
            int codecCount = MediaCodecList.getCodecCount();
            for (int i = 0; i < codecCount; i++) {
                try {
                    arrayList.add(MediaCodecList.getCodecInfoAt(i));
                } catch (IllegalArgumentException e) {
                    Logging.e("MediaCodecUtils", "Cannot retrieve codec info", e);
                }
            }
            Collections.sort(arrayList, $$Lambda$MediaCodecUtils$2a7tPlee9qsf_7lHzR8WPINZSpo.INSTANCE);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return arrayList;
    }

    static Integer selectColorFormat(int[] iArr, MediaCodecInfo.CodecCapabilities codecCapabilities) {
        for (int i : iArr) {
            for (int i2 : codecCapabilities.colorFormats) {
                if (i2 == i) {
                    return Integer.valueOf(i2);
                }
            }
        }
        return null;
    }

    static boolean codecSupportsType(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType) {
        for (String equals : mediaCodecInfo.getSupportedTypes()) {
            if (videoCodecMimeType.mimeType().equals(equals)) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: org.webrtc.MediaCodecUtils$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$VideoCodecMimeType;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                org.webrtc.VideoCodecMimeType[] r0 = org.webrtc.VideoCodecMimeType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$webrtc$VideoCodecMimeType = r0
                org.webrtc.VideoCodecMimeType r1 = org.webrtc.VideoCodecMimeType.VP8     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$webrtc$VideoCodecMimeType     // Catch:{ NoSuchFieldError -> 0x001d }
                org.webrtc.VideoCodecMimeType r1 = org.webrtc.VideoCodecMimeType.VP9     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$webrtc$VideoCodecMimeType     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.webrtc.VideoCodecMimeType r1 = org.webrtc.VideoCodecMimeType.H265     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$org$webrtc$VideoCodecMimeType     // Catch:{ NoSuchFieldError -> 0x0033 }
                org.webrtc.VideoCodecMimeType r1 = org.webrtc.VideoCodecMimeType.H264     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.MediaCodecUtils.AnonymousClass1.<clinit>():void");
        }
    }

    static Map<String, String> getCodecProperties(VideoCodecMimeType videoCodecMimeType, boolean z) {
        int i = AnonymousClass1.$SwitchMap$org$webrtc$VideoCodecMimeType[videoCodecMimeType.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return new HashMap();
        }
        if (i == 4) {
            return H264Utils.getDefaultH264Params(z);
        }
        throw new IllegalArgumentException("Unsupported codec: " + videoCodecMimeType);
    }

    static boolean isHardwareAccelerated(MediaCodecInfo mediaCodecInfo) {
        if (Build.VERSION.SDK_INT >= 29) {
            return isHardwareAcceleratedQOrHigher(mediaCodecInfo);
        }
        return !isSoftwareOnly(mediaCodecInfo);
    }

    @TargetApi(29)
    private static boolean isHardwareAcceleratedQOrHigher(MediaCodecInfo mediaCodecInfo) {
        return mediaCodecInfo.isHardwareAccelerated();
    }

    static boolean isSoftwareOnly(MediaCodecInfo mediaCodecInfo) {
        if (Build.VERSION.SDK_INT >= 29) {
            return isSoftwareOnlyQOrHigher(mediaCodecInfo);
        }
        String name = mediaCodecInfo.getName();
        for (String startsWith : SOFTWARE_IMPLEMENTATION_PREFIXES) {
            if (name.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(29)
    private static boolean isSoftwareOnlyQOrHigher(MediaCodecInfo mediaCodecInfo) {
        return mediaCodecInfo.isSoftwareOnly();
    }

    private MediaCodecUtils() {
    }
}
