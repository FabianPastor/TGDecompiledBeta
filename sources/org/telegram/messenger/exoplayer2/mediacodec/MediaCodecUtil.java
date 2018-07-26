package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecList;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.C0621C;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.tgnet.ConnectionsManager;

@SuppressLint({"InlinedApi"})
@TargetApi(16)
public final class MediaCodecUtil {
    private static final SparseIntArray AVC_LEVEL_NUMBER_TO_CONST = new SparseIntArray();
    private static final SparseIntArray AVC_PROFILE_NUMBER_TO_CONST = new SparseIntArray();
    private static final String CODEC_ID_AVC1 = "avc1";
    private static final String CODEC_ID_AVC2 = "avc2";
    private static final String CODEC_ID_HEV1 = "hev1";
    private static final String CODEC_ID_HVC1 = "hvc1";
    private static final String GOOGLE_RAW_DECODER_NAME = "OMX.google.raw.decoder";
    private static final Map<String, Integer> HEVC_CODEC_STRING_TO_PROFILE_LEVEL = new HashMap();
    private static final String MTK_RAW_DECODER_NAME = "OMX.MTK.AUDIO.DECODER.RAW";
    private static final MediaCodecInfo PASSTHROUGH_DECODER_INFO = MediaCodecInfo.newPassthroughInstance(GOOGLE_RAW_DECODER_NAME);
    private static final Pattern PROFILE_PATTERN = Pattern.compile("^\\D?(\\d+)$");
    private static final String TAG = "MediaCodecUtil";
    private static final HashMap<CodecKey, List<MediaCodecInfo>> decoderInfosCache = new HashMap();
    private static int maxH264DecodableFrameSize = -1;

    private static final class CodecKey {
        public final String mimeType;
        public final boolean secure;

        public CodecKey(String mimeType, boolean secure) {
            this.mimeType = mimeType;
            this.secure = secure;
        }

        public int hashCode() {
            return (((this.mimeType == null ? 0 : this.mimeType.hashCode()) + 31) * 31) + (this.secure ? 1231 : 1237);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != CodecKey.class) {
                return false;
            }
            CodecKey other = (CodecKey) obj;
            if (TextUtils.equals(this.mimeType, other.mimeType) && this.secure == other.secure) {
                return true;
            }
            return false;
        }
    }

    public static class DecoderQueryException extends Exception {
        private DecoderQueryException(Throwable cause) {
            super("Failed to query underlying media codecs", cause);
        }
    }

    private interface MediaCodecListCompat {
        int getCodecCount();

        MediaCodecInfo getCodecInfoAt(int i);

        boolean isSecurePlaybackSupported(String str, CodecCapabilities codecCapabilities);

        boolean secureDecodersExplicit();
    }

    private static final class MediaCodecListCompatV16 implements MediaCodecListCompat {
        private MediaCodecListCompatV16() {
        }

        public int getCodecCount() {
            return MediaCodecList.getCodecCount();
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            return MediaCodecList.getCodecInfoAt(index);
        }

        public boolean secureDecodersExplicit() {
            return false;
        }

        public boolean isSecurePlaybackSupported(String mimeType, CodecCapabilities capabilities) {
            return "video/avc".equals(mimeType);
        }
    }

    @TargetApi(21)
    private static final class MediaCodecListCompatV21 implements MediaCodecListCompat {
        private final int codecKind;
        private MediaCodecInfo[] mediaCodecInfos;

        public MediaCodecListCompatV21(boolean includeSecure) {
            this.codecKind = includeSecure ? 1 : 0;
        }

        public int getCodecCount() {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos.length;
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos[index];
        }

        public boolean secureDecodersExplicit() {
            return true;
        }

        public boolean isSecurePlaybackSupported(String mimeType, CodecCapabilities capabilities) {
            return capabilities.isFeatureSupported("secure-playback");
        }

        private void ensureMediaCodecInfosInitialized() {
            if (this.mediaCodecInfos == null) {
                this.mediaCodecInfos = new MediaCodecList(this.codecKind).getCodecInfos();
            }
        }
    }

    static {
        AVC_PROFILE_NUMBER_TO_CONST.put(66, 1);
        AVC_PROFILE_NUMBER_TO_CONST.put(77, 2);
        AVC_PROFILE_NUMBER_TO_CONST.put(88, 4);
        AVC_PROFILE_NUMBER_TO_CONST.put(100, 8);
        AVC_LEVEL_NUMBER_TO_CONST.put(10, 1);
        AVC_LEVEL_NUMBER_TO_CONST.put(11, 4);
        AVC_LEVEL_NUMBER_TO_CONST.put(12, 8);
        AVC_LEVEL_NUMBER_TO_CONST.put(13, 16);
        AVC_LEVEL_NUMBER_TO_CONST.put(20, 32);
        AVC_LEVEL_NUMBER_TO_CONST.put(21, 64);
        AVC_LEVEL_NUMBER_TO_CONST.put(22, 128);
        AVC_LEVEL_NUMBER_TO_CONST.put(30, 256);
        AVC_LEVEL_NUMBER_TO_CONST.put(31, 512);
        AVC_LEVEL_NUMBER_TO_CONST.put(32, 1024);
        AVC_LEVEL_NUMBER_TO_CONST.put(40, 2048);
        AVC_LEVEL_NUMBER_TO_CONST.put(41, 4096);
        AVC_LEVEL_NUMBER_TO_CONST.put(42, MessagesController.UPDATE_MASK_CHANNEL);
        AVC_LEVEL_NUMBER_TO_CONST.put(50, MessagesController.UPDATE_MASK_CHAT_ADMINS);
        AVC_LEVEL_NUMBER_TO_CONST.put(51, 32768);
        AVC_LEVEL_NUMBER_TO_CONST.put(52, C0621C.DEFAULT_BUFFER_SEGMENT_SIZE);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L30", Integer.valueOf(1));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L60", Integer.valueOf(4));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L63", Integer.valueOf(16));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L90", Integer.valueOf(64));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L93", Integer.valueOf(256));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L120", Integer.valueOf(1024));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L123", Integer.valueOf(4096));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L150", Integer.valueOf(MessagesController.UPDATE_MASK_CHAT_ADMINS));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L153", Integer.valueOf(C0621C.DEFAULT_BUFFER_SEGMENT_SIZE));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L156", Integer.valueOf(262144));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L180", Integer.valueOf(ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L183", Integer.valueOf(4194304));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L186", Integer.valueOf(16777216));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H30", Integer.valueOf(2));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H60", Integer.valueOf(8));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H63", Integer.valueOf(32));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H90", Integer.valueOf(128));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H93", Integer.valueOf(512));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H120", Integer.valueOf(2048));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H123", Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H150", Integer.valueOf(32768));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H153", Integer.valueOf(131072));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H156", Integer.valueOf(524288));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H180", Integer.valueOf(2097152));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H183", Integer.valueOf(8388608));
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H186", Integer.valueOf(ConnectionsManager.FileTypeVideo));
    }

    private MediaCodecUtil() {
    }

    public static void warmDecoderInfoCache(String mimeType, boolean secure) {
        try {
            getDecoderInfos(mimeType, secure);
        } catch (DecoderQueryException e) {
            Log.e(TAG, "Codec warming failed", e);
        }
    }

    public static MediaCodecInfo getPassthroughDecoderInfo() {
        return PASSTHROUGH_DECODER_INFO;
    }

    public static MediaCodecInfo getDecoderInfo(String mimeType, boolean secure) throws DecoderQueryException {
        List<MediaCodecInfo> decoderInfos = getDecoderInfos(mimeType, secure);
        return decoderInfos.isEmpty() ? null : (MediaCodecInfo) decoderInfos.get(0);
    }

    public static synchronized List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean secure) throws DecoderQueryException {
        List<MediaCodecInfo> cachedDecoderInfos;
        synchronized (MediaCodecUtil.class) {
            CodecKey key = new CodecKey(mimeType, secure);
            cachedDecoderInfos = (List) decoderInfosCache.get(key);
            if (cachedDecoderInfos == null) {
                MediaCodecListCompat mediaCodecList;
                if (Util.SDK_INT >= 21) {
                    mediaCodecList = new MediaCodecListCompatV21(secure);
                } else {
                    mediaCodecList = new MediaCodecListCompatV16();
                }
                ArrayList<MediaCodecInfo> decoderInfos = getDecoderInfosInternal(key, mediaCodecList, mimeType);
                if (secure && decoderInfos.isEmpty() && 21 <= Util.SDK_INT && Util.SDK_INT <= 23) {
                    mediaCodecList = new MediaCodecListCompatV16();
                    decoderInfos = getDecoderInfosInternal(key, mediaCodecList, mimeType);
                    if (!decoderInfos.isEmpty()) {
                        Log.w(TAG, "MediaCodecList API didn't list secure decoder for: " + mimeType + ". Assuming: " + ((MediaCodecInfo) decoderInfos.get(0)).name);
                    }
                }
                if (MimeTypes.AUDIO_E_AC3_JOC.equals(mimeType)) {
                    decoderInfos.addAll(getDecoderInfosInternal(new CodecKey(MimeTypes.AUDIO_E_AC3, key.secure), mediaCodecList, mimeType));
                }
                applyWorkarounds(decoderInfos);
                List<MediaCodecInfo> unmodifiableDecoderInfos = Collections.unmodifiableList(decoderInfos);
                decoderInfosCache.put(key, unmodifiableDecoderInfos);
                cachedDecoderInfos = unmodifiableDecoderInfos;
            }
        }
        return cachedDecoderInfos;
    }

    public static int maxH264DecodableFrameSize() throws DecoderQueryException {
        int i = 0;
        if (maxH264DecodableFrameSize == -1) {
            int result = 0;
            MediaCodecInfo decoderInfo = getDecoderInfo("video/avc", false);
            if (decoderInfo != null) {
                CodecProfileLevel[] profileLevels = decoderInfo.getProfileLevels();
                int length = profileLevels.length;
                while (i < length) {
                    result = Math.max(avcLevelToMaxFrameSize(profileLevels[i].level), result);
                    i++;
                }
                result = Math.max(result, Util.SDK_INT >= 21 ? 345600 : 172800);
            }
            maxH264DecodableFrameSize = result;
        }
        return maxH264DecodableFrameSize;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Pair<Integer, Integer> getCodecProfileAndLevel(String codec) {
        int i = 0;
        if (codec == null) {
            return null;
        }
        String[] parts = codec.split("\\.");
        String str = parts[0];
        switch (str.hashCode()) {
            case 3006243:
                if (str.equals("avc1")) {
                    i = 2;
                    break;
                }
            case 3006244:
                if (str.equals(CODEC_ID_AVC2)) {
                    i = 3;
                    break;
                }
            case 3199032:
                if (str.equals("hev1")) {
                    break;
                }
            case 3214780:
                if (str.equals("hvc1")) {
                    i = 1;
                    break;
                }
            default:
                i = -1;
                break;
        }
        switch (i) {
            case 0:
            case 1:
                return getHevcProfileAndLevel(codec, parts);
            case 2:
            case 3:
                return getAvcProfileAndLevel(codec, parts);
            default:
                return null;
        }
    }

    private static ArrayList<MediaCodecInfo> getDecoderInfosInternal(CodecKey key, MediaCodecListCompat mediaCodecList, String requestedMimeType) throws DecoderQueryException {
        String codecName;
        try {
            ArrayList<MediaCodecInfo> decoderInfos = new ArrayList();
            String mimeType = key.mimeType;
            int numberOfCodecs = mediaCodecList.getCodecCount();
            boolean secureDecodersExplicit = mediaCodecList.secureDecodersExplicit();
            loop0:
            for (int i = 0; i < numberOfCodecs; i++) {
                MediaCodecInfo codecInfo = mediaCodecList.getCodecInfoAt(i);
                codecName = codecInfo.getName();
                if (isCodecUsableDecoder(codecInfo, codecName, secureDecodersExplicit, requestedMimeType)) {
                    for (String supportedType : codecInfo.getSupportedTypes()) {
                        if (supportedType.equalsIgnoreCase(mimeType)) {
                            CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(supportedType);
                            boolean secure = mediaCodecList.isSecurePlaybackSupported(mimeType, capabilities);
                            boolean forceDisableAdaptive = codecNeedsDisableAdaptationWorkaround(codecName);
                            if ((!secureDecodersExplicit || key.secure != secure) && (secureDecodersExplicit || key.secure)) {
                                if (!secureDecodersExplicit && secure) {
                                    decoderInfos.add(MediaCodecInfo.newInstance(codecName + ".secure", mimeType, capabilities, forceDisableAdaptive, true));
                                    break loop0;
                                }
                            }
                            decoderInfos.add(MediaCodecInfo.newInstance(codecName, mimeType, capabilities, forceDisableAdaptive, false));
                        }
                    }
                    continue;
                }
            }
            return decoderInfos;
        } catch (Exception e) {
            if (Util.SDK_INT > 23 || decoderInfos.isEmpty()) {
                Log.e(TAG, "Failed to query codec " + codecName + " (" + supportedType + ")");
                throw e;
            }
            Log.e(TAG, "Skipping codec " + codecName + " (failed to query capabilities)");
        } catch (Exception e2) {
            throw new DecoderQueryException(e2);
        }
    }

    private static boolean isCodecUsableDecoder(MediaCodecInfo info, String name, boolean secureDecodersExplicit, String requestedMimeType) {
        if (info.isEncoder()) {
            return false;
        }
        if (!secureDecodersExplicit && name.endsWith(".secure")) {
            return false;
        }
        if (Util.SDK_INT < 21 && ("CIPAACDecoder".equals(name) || "CIPMP3Decoder".equals(name) || "CIPVorbisDecoder".equals(name) || "CIPAMRNBDecoder".equals(name) || "AACDecoder".equals(name) || "MP3Decoder".equals(name))) {
            return false;
        }
        if (Util.SDK_INT < 18 && "OMX.SEC.MP3.Decoder".equals(name)) {
            return false;
        }
        if (Util.SDK_INT < 18 && "OMX.MTK.AUDIO.DECODER.AAC".equals(name)) {
            if ("a70".equals(Util.DEVICE)) {
                return false;
            }
            if ("Xiaomi".equals(Util.MANUFACTURER) && Util.DEVICE.startsWith("HM")) {
                return false;
            }
        }
        if (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.mp3".equals(name) && ("dlxu".equals(Util.DEVICE) || "protou".equals(Util.DEVICE) || "ville".equals(Util.DEVICE) || "villeplus".equals(Util.DEVICE) || "villec2".equals(Util.DEVICE) || Util.DEVICE.startsWith("gee") || "C6602".equals(Util.DEVICE) || "C6603".equals(Util.DEVICE) || "C6606".equals(Util.DEVICE) || "C6616".equals(Util.DEVICE) || "L36h".equals(Util.DEVICE) || "SO-02E".equals(Util.DEVICE))) {
            return false;
        }
        if (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.aac".equals(name) && ("C1504".equals(Util.DEVICE) || "C1505".equals(Util.DEVICE) || "C1604".equals(Util.DEVICE) || "C1605".equals(Util.DEVICE))) {
            return false;
        }
        if (Util.SDK_INT < 24 && (("OMX.SEC.aac.dec".equals(name) || "OMX.Exynos.AAC.Decoder".equals(name)) && "samsung".equals(Util.MANUFACTURER) && (Util.DEVICE.startsWith("zeroflte") || Util.DEVICE.startsWith("zerolte") || Util.DEVICE.startsWith("zenlte") || "SC-05G".equals(Util.DEVICE) || "marinelteatt".equals(Util.DEVICE) || "404SC".equals(Util.DEVICE) || "SC-04G".equals(Util.DEVICE) || "SCV31".equals(Util.DEVICE)))) {
            return false;
        }
        if (Util.SDK_INT <= 19 && "OMX.SEC.vp8.dec".equals(name) && "samsung".equals(Util.MANUFACTURER) && (Util.DEVICE.startsWith("d2") || Util.DEVICE.startsWith("serrano") || Util.DEVICE.startsWith("jflte") || Util.DEVICE.startsWith("santos") || Util.DEVICE.startsWith("t0"))) {
            return false;
        }
        if (Util.SDK_INT <= 19 && Util.DEVICE.startsWith("jflte") && "OMX.qcom.video.decoder.vp8".equals(name)) {
            return false;
        }
        if (MimeTypes.AUDIO_E_AC3_JOC.equals(requestedMimeType) && "OMX.MTK.AUDIO.DECODER.DSPAC3".equals(name)) {
            return false;
        }
        return true;
    }

    private static void applyWorkarounds(List<MediaCodecInfo> decoderInfos) {
        if (Util.SDK_INT < 26 && decoderInfos.size() > 1 && MTK_RAW_DECODER_NAME.equals(((MediaCodecInfo) decoderInfos.get(0)).name)) {
            for (int i = 1; i < decoderInfos.size(); i++) {
                MediaCodecInfo decoderInfo = (MediaCodecInfo) decoderInfos.get(i);
                if (GOOGLE_RAW_DECODER_NAME.equals(decoderInfo.name)) {
                    decoderInfos.remove(i);
                    decoderInfos.add(0, decoderInfo);
                    return;
                }
            }
        }
    }

    private static boolean codecNeedsDisableAdaptationWorkaround(String name) {
        return Util.SDK_INT <= 22 && (("ODROID-XU3".equals(Util.MODEL) || "Nexus 10".equals(Util.MODEL)) && ("OMX.Exynos.AVC.Decoder".equals(name) || "OMX.Exynos.AVC.Decoder.secure".equals(name)));
    }

    private static Pair<Integer, Integer> getHevcProfileAndLevel(String codec, String[] parts) {
        if (parts.length < 4) {
            Log.w(TAG, "Ignoring malformed HEVC codec string: " + codec);
            return null;
        }
        Matcher matcher = PROFILE_PATTERN.matcher(parts[1]);
        if (matcher.matches()) {
            int profile;
            String profileString = matcher.group(1);
            if ("1".equals(profileString)) {
                profile = 1;
            } else if ("2".equals(profileString)) {
                profile = 2;
            } else {
                Log.w(TAG, "Unknown HEVC profile string: " + profileString);
                return null;
            }
            Integer level = (Integer) HEVC_CODEC_STRING_TO_PROFILE_LEVEL.get(parts[3]);
            if (level != null) {
                return new Pair(Integer.valueOf(profile), level);
            }
            Log.w(TAG, "Unknown HEVC level string: " + matcher.group(1));
            return null;
        }
        Log.w(TAG, "Ignoring malformed HEVC codec string: " + codec);
        return null;
    }

    private static Pair<Integer, Integer> getAvcProfileAndLevel(String codec, String[] codecsParts) {
        if (codecsParts.length < 2) {
            Log.w(TAG, "Ignoring malformed AVC codec string: " + codec);
            return null;
        }
        try {
            Integer profileInteger;
            Integer levelInteger;
            if (codecsParts[1].length() == 6) {
                profileInteger = Integer.valueOf(Integer.parseInt(codecsParts[1].substring(0, 2), 16));
                levelInteger = Integer.valueOf(Integer.parseInt(codecsParts[1].substring(4), 16));
            } else if (codecsParts.length >= 3) {
                profileInteger = Integer.valueOf(Integer.parseInt(codecsParts[1]));
                levelInteger = Integer.valueOf(Integer.parseInt(codecsParts[2]));
            } else {
                Log.w(TAG, "Ignoring malformed AVC codec string: " + codec);
                return null;
            }
            int profile = AVC_PROFILE_NUMBER_TO_CONST.get(profileInteger.intValue(), -1);
            if (profile == -1) {
                Log.w(TAG, "Unknown AVC profile: " + profileInteger);
                return null;
            }
            int level = AVC_LEVEL_NUMBER_TO_CONST.get(levelInteger.intValue(), -1);
            if (level != -1) {
                return new Pair(Integer.valueOf(profile), Integer.valueOf(level));
            }
            Log.w(TAG, "Unknown AVC level: " + levelInteger);
            return null;
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed AVC codec string: " + codec);
            return null;
        }
    }

    private static int avcLevelToMaxFrameSize(int avcLevel) {
        switch (avcLevel) {
            case 1:
            case 2:
                return 25344;
            case 8:
                return 101376;
            case 16:
                return 101376;
            case 32:
                return 101376;
            case 64:
                return 202752;
            case 128:
                return 414720;
            case 256:
                return 414720;
            case 512:
                return 921600;
            case 1024:
                return 1310720;
            case 2048:
                return 2097152;
            case 4096:
                return 2097152;
            case MessagesController.UPDATE_MASK_CHANNEL /*8192*/:
                return 2228224;
            case MessagesController.UPDATE_MASK_CHAT_ADMINS /*16384*/:
                return 5652480;
            case 32768:
                return 9437184;
            case C0621C.DEFAULT_BUFFER_SEGMENT_SIZE /*65536*/:
                return 9437184;
            default:
                return -1;
        }
    }
}
