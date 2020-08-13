package org.webrtc;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import androidx.annotation.RecentlyNonNull;
import j$.lang.Iterable;
import j$.util.Collection;
import j$.util.Set;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.webrtc.EglBase;
import org.webrtc.EglBase14;
import org.webrtc.VideoEncoderFactory;

public class HardwareVideoEncoderFactory implements VideoEncoderFactory {
    private static final List<String> H264_HW_EXCEPTION_MODELS = Arrays.asList(new String[]{"SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4"});
    private static Set<String> HW_EXCEPTION_MODELS = new HashSet<String>() {
        public /* synthetic */ void forEach(@RecentlyNonNull Consumer<? super T> consumer) {
            Iterable.CC.$default$forEach(this, consumer);
        }

        @RecentlyNonNull
        public /* synthetic */ Stream<E> parallelStream() {
            return Collection.CC.$default$parallelStream(this);
        }

        public /* synthetic */ boolean removeIf(@RecentlyNonNull Predicate<? super E> predicate) {
            return Collection.CC.$default$removeIf(this, predicate);
        }

        @RecentlyNonNull
        public /* synthetic */ Spliterator<E> spliterator() {
            return Set.CC.$default$spliterator(this);
        }

        @RecentlyNonNull
        public /* synthetic */ Stream<E> stream() {
            return Collection.CC.$default$stream(this);
        }

        {
            add("SM-A310F");
            add("SM-A310F/DS");
            add("SM-A310Y");
            add("SM-A310M");
            add("SM-G920F");
            add("SM-G920FD");
            add("SM-G920FQ");
            add("SM-G920I");
            add("SM-G920A");
            add("SM-G920T");
            add("SM-G930F");
            add("SM-G930FD");
            add("SM-G930W8");
            add("SM-G930S");
            add("SM-G930K");
            add("SM-G930L");
            add("SM-G935F");
            add("SM-G935FD");
            add("SM-G935W8");
            add("SM-G935S");
            add("SM-G935K");
            add("SM-G935L");
        }
    };
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "HardwareVideoEncoderFactory";
    private final Predicate<MediaCodecInfo> codecAllowedPredicate;
    private final boolean enableH264HighProfile;
    private final boolean enableIntelVp8Encoder;
    private final EglBase14.Context sharedContext;

    @CalledByNative
    public /* synthetic */ VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return VideoEncoderFactory.CC.$default$getEncoderSelector(this);
    }

    @CalledByNative
    public /* synthetic */ VideoCodecInfo[] getImplementations() {
        return VideoEncoderFactory.CC.$default$getImplementations(this);
    }

    public HardwareVideoEncoderFactory(EglBase.Context context, boolean z, boolean z2) {
        this(context, z, z2, (Predicate<MediaCodecInfo>) null);
    }

    public HardwareVideoEncoderFactory(EglBase.Context context, boolean z, boolean z2, Predicate<MediaCodecInfo> predicate) {
        if (context instanceof EglBase14.Context) {
            this.sharedContext = (EglBase14.Context) context;
        } else {
            Logging.w("HardwareVideoEncoderFactory", "No shared EglBase.Context.  Encoders will not use texture mode.");
            this.sharedContext = null;
        }
        this.enableIntelVp8Encoder = z;
        this.enableH264HighProfile = z2;
        this.codecAllowedPredicate = predicate;
    }

    @Deprecated
    public HardwareVideoEncoderFactory(boolean z, boolean z2) {
        this((EglBase.Context) null, z, z2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r5 = org.webrtc.VideoCodecMimeType.valueOf(r14.name);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.webrtc.VideoEncoder createEncoder(org.webrtc.VideoCodecInfo r14) {
        /*
            r13 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 19
            if (r0 >= r2) goto L_0x0008
            return r1
        L_0x0008:
            java.lang.String r0 = r14.name
            org.webrtc.VideoCodecMimeType r5 = org.webrtc.VideoCodecMimeType.valueOf(r0)
            android.media.MediaCodecInfo r0 = r13.findCodecForType(r5)
            if (r0 != 0) goto L_0x0015
            return r1
        L_0x0015:
            java.lang.String r4 = r0.getName()
            java.lang.String r2 = r5.mimeType()
            int[] r3 = org.webrtc.MediaCodecUtils.TEXTURE_COLOR_FORMATS
            android.media.MediaCodecInfo$CodecCapabilities r6 = r0.getCapabilitiesForType(r2)
            java.lang.Integer r6 = org.webrtc.MediaCodecUtils.selectColorFormat(r3, r6)
            int[] r3 = org.webrtc.MediaCodecUtils.ENCODER_COLOR_FORMATS
            android.media.MediaCodecInfo$CodecCapabilities r2 = r0.getCapabilitiesForType(r2)
            java.lang.Integer r7 = org.webrtc.MediaCodecUtils.selectColorFormat(r3, r2)
            org.webrtc.VideoCodecMimeType r2 = org.webrtc.VideoCodecMimeType.H264
            if (r5 != r2) goto L_0x0059
            java.util.Map<java.lang.String, java.lang.String> r2 = r14.params
            r3 = 1
            java.util.Map r3 = org.webrtc.MediaCodecUtils.getCodecProperties(r5, r3)
            boolean r2 = org.webrtc.H264Utils.isSameH264Profile(r2, r3)
            java.util.Map<java.lang.String, java.lang.String> r3 = r14.params
            r8 = 0
            java.util.Map r8 = org.webrtc.MediaCodecUtils.getCodecProperties(r5, r8)
            boolean r3 = org.webrtc.H264Utils.isSameH264Profile(r3, r8)
            if (r2 != 0) goto L_0x0050
            if (r3 != 0) goto L_0x0050
            return r1
        L_0x0050:
            if (r2 == 0) goto L_0x0059
            boolean r0 = r13.isH264HighProfileSupported(r0)
            if (r0 != 0) goto L_0x0059
            return r1
        L_0x0059:
            org.webrtc.HardwareVideoEncoder r0 = new org.webrtc.HardwareVideoEncoder
            org.webrtc.MediaCodecWrapperFactoryImpl r3 = new org.webrtc.MediaCodecWrapperFactoryImpl
            r3.<init>()
            java.util.Map<java.lang.String, java.lang.String> r8 = r14.params
            int r9 = r13.getKeyFrameIntervalSec(r5)
            int r10 = r13.getForcedKeyFrameIntervalMs(r5, r4)
            org.webrtc.BitrateAdjuster r11 = r13.createBitrateAdjuster(r5, r4)
            org.webrtc.EglBase14$Context r12 = r13.sharedContext
            r2 = r0
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoEncoderFactory.createEncoder(org.webrtc.VideoCodecInfo):org.webrtc.VideoEncoder");
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        if (Build.VERSION.SDK_INT < 19) {
            return new VideoCodecInfo[0];
        }
        ArrayList arrayList = new ArrayList();
        VideoCodecMimeType[] videoCodecMimeTypeArr = {VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264, VideoCodecMimeType.H265};
        for (int i = 0; i < 4; i++) {
            VideoCodecMimeType videoCodecMimeType = videoCodecMimeTypeArr[i];
            MediaCodecInfo findCodecForType = findCodecForType(videoCodecMimeType);
            if (findCodecForType != null) {
                String name = videoCodecMimeType.name();
                if (videoCodecMimeType == VideoCodecMimeType.H264 && isH264HighProfileSupported(findCodecForType)) {
                    arrayList.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(videoCodecMimeType, true)));
                }
                arrayList.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(videoCodecMimeType, false)));
            }
        }
        return (VideoCodecInfo[]) arrayList.toArray(new VideoCodecInfo[arrayList.size()]);
    }

    private MediaCodecInfo findCodecForType(VideoCodecMimeType videoCodecMimeType) {
        int i = 0;
        while (true) {
            MediaCodecInfo mediaCodecInfo = null;
            if (i >= MediaCodecList.getCodecCount()) {
                return null;
            }
            try {
                mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                Logging.e("HardwareVideoEncoderFactory", "Cannot retrieve encoder codec info", e);
            }
            if (mediaCodecInfo != null && mediaCodecInfo.isEncoder() && isSupportedCodec(mediaCodecInfo, videoCodecMimeType)) {
                return mediaCodecInfo;
            }
            i++;
        }
    }

    private boolean isSupportedCodec(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType) {
        if (MediaCodecUtils.codecSupportsType(mediaCodecInfo, videoCodecMimeType) && MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, mediaCodecInfo.getCapabilitiesForType(videoCodecMimeType.mimeType())) != null && isHardwareSupportedInCurrentSdk(mediaCodecInfo, videoCodecMimeType) && isMediaCodecAllowed(mediaCodecInfo)) {
            return true;
        }
        return false;
    }

    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType) {
        if (HW_EXCEPTION_MODELS.contains(Build.MODEL)) {
            return false;
        }
        int i = AnonymousClass2.$SwitchMap$org$webrtc$VideoCodecMimeType[videoCodecMimeType.ordinal()];
        if (i == 1) {
            return isHardwareSupportedInCurrentSdkVp8(mediaCodecInfo);
        }
        if (i == 2) {
            return isHardwareSupportedInCurrentSdkVp9(mediaCodecInfo);
        }
        if (i == 3) {
            return isHardwareSupportedInCurrentSdkH264(mediaCodecInfo);
        }
        if (i != 4) {
            return false;
        }
        return isHardwareSupportedInCurrentSdkH265(mediaCodecInfo);
    }

    /* renamed from: org.webrtc.HardwareVideoEncoderFactory$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
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
                org.webrtc.VideoCodecMimeType r1 = org.webrtc.VideoCodecMimeType.H264     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$org$webrtc$VideoCodecMimeType     // Catch:{ NoSuchFieldError -> 0x0033 }
                org.webrtc.VideoCodecMimeType r1 = org.webrtc.VideoCodecMimeType.H265     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoEncoderFactory.AnonymousClass2.<clinit>():void");
        }
    }

    private boolean isHardwareSupportedInCurrentSdkVp8(MediaCodecInfo mediaCodecInfo) {
        String name = mediaCodecInfo.getName();
        return (name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name.startsWith("OMX.hisi.") && Build.VERSION.SDK_INT >= 19) || ((name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 23) || (name.startsWith("OMX.Intel.") && Build.VERSION.SDK_INT >= 21 && this.enableIntelVp8Encoder));
    }

    private boolean isHardwareSupportedInCurrentSdkVp9(MediaCodecInfo mediaCodecInfo) {
        String name = mediaCodecInfo.getName();
        return (name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.") || name.startsWith("OMX.hisi.")) && Build.VERSION.SDK_INT >= 24;
    }

    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo mediaCodecInfo) {
        if (H264_HW_EXCEPTION_MODELS.contains(Build.MODEL)) {
            return false;
        }
        String name = mediaCodecInfo.getName();
        if ((!name.startsWith("OMX.qcom.") || Build.VERSION.SDK_INT < 19) && (!name.startsWith("OMX.Exynos.") || Build.VERSION.SDK_INT < 21)) {
            return false;
        }
        return true;
    }

    private boolean isHardwareSupportedInCurrentSdkH265(MediaCodecInfo mediaCodecInfo) {
        String name = mediaCodecInfo.getName();
        return (name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 21);
    }

    private boolean isMediaCodecAllowed(MediaCodecInfo mediaCodecInfo) {
        Predicate<MediaCodecInfo> predicate = this.codecAllowedPredicate;
        if (predicate == null) {
            return true;
        }
        return predicate.test(mediaCodecInfo);
    }

    private int getKeyFrameIntervalSec(VideoCodecMimeType videoCodecMimeType) {
        int i = AnonymousClass2.$SwitchMap$org$webrtc$VideoCodecMimeType[videoCodecMimeType.ordinal()];
        if (i == 1 || i == 2) {
            return 100;
        }
        if (i == 3 || i == 4) {
            return 20;
        }
        throw new IllegalArgumentException("Unsupported VideoCodecMimeType " + videoCodecMimeType);
    }

    private int getForcedKeyFrameIntervalMs(VideoCodecMimeType videoCodecMimeType, String str) {
        if (videoCodecMimeType != VideoCodecMimeType.VP8 || !str.startsWith("OMX.qcom.")) {
            return 0;
        }
        int i = Build.VERSION.SDK_INT;
        if (!(i == 21 || i == 22)) {
            if (i == 23) {
                return 20000;
            }
            return i > 23 ? 15000 : 0;
        }
    }

    private BitrateAdjuster createBitrateAdjuster(VideoCodecMimeType videoCodecMimeType, String str) {
        if (!str.startsWith("OMX.Exynos.")) {
            return new BaseBitrateAdjuster();
        }
        if (videoCodecMimeType == VideoCodecMimeType.VP8) {
            return new DynamicBitrateAdjuster();
        }
        return new FramerateBitrateAdjuster();
    }

    private boolean isH264HighProfileSupported(MediaCodecInfo mediaCodecInfo) {
        return this.enableH264HighProfile && Build.VERSION.SDK_INT > 23 && mediaCodecInfo.getName().startsWith("OMX.Exynos.");
    }
}
