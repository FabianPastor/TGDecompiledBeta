package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.util.Log;
import android.util.Pair;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo {
    public static final String TAG = "MediaCodecInfo";
    public final boolean adaptive;
    private final CodecCapabilities capabilities;
    private final String mimeType;
    public final String name;
    public final boolean secure;
    public final boolean tunneling;

    public static MediaCodecInfo newPassthroughInstance(String str) {
        return new MediaCodecInfo(str, null, null, false, false);
    }

    public static MediaCodecInfo newInstance(String str, String str2, CodecCapabilities codecCapabilities) {
        return new MediaCodecInfo(str, str2, codecCapabilities, false, false);
    }

    public static MediaCodecInfo newInstance(String str, String str2, CodecCapabilities codecCapabilities, boolean z, boolean z2) {
        return new MediaCodecInfo(str, str2, codecCapabilities, z, z2);
    }

    private MediaCodecInfo(String str, String str2, CodecCapabilities codecCapabilities, boolean z, boolean z2) {
        this.name = (String) Assertions.checkNotNull(str);
        this.mimeType = str2;
        this.capabilities = codecCapabilities;
        str = null;
        z = (z || codecCapabilities == null || !isAdaptive(codecCapabilities)) ? false : true;
        this.adaptive = z;
        z = codecCapabilities != null && isTunneling(codecCapabilities);
        this.tunneling = z;
        if (z2 || !(codecCapabilities == null || isSecure(codecCapabilities) == null)) {
            str = 1;
        }
        this.secure = str;
    }

    public CodecProfileLevel[] getProfileLevels() {
        if (this.capabilities != null) {
            if (this.capabilities.profileLevels != null) {
                return this.capabilities.profileLevels;
            }
        }
        return new CodecProfileLevel[0];
    }

    public boolean isCodecSupported(String str) {
        if (str != null) {
            if (this.mimeType != null) {
                String mediaMimeType = MimeTypes.getMediaMimeType(str);
                if (mediaMimeType == null) {
                    return true;
                }
                StringBuilder stringBuilder;
                if (this.mimeType.equals(mediaMimeType)) {
                    Pair codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(str);
                    if (codecProfileAndLevel == null) {
                        return true;
                    }
                    for (CodecProfileLevel codecProfileLevel : getProfileLevels()) {
                        if (codecProfileLevel.profile == ((Integer) codecProfileAndLevel.first).intValue() && codecProfileLevel.level >= ((Integer) codecProfileAndLevel.second).intValue()) {
                            return true;
                        }
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("codec.profileLevel, ");
                    stringBuilder.append(str);
                    stringBuilder.append(", ");
                    stringBuilder.append(mediaMimeType);
                    logNoSupport(stringBuilder.toString());
                    return false;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("codec.mime ");
                stringBuilder.append(str);
                stringBuilder.append(", ");
                stringBuilder.append(mediaMimeType);
                logNoSupport(stringBuilder.toString());
                return false;
            }
        }
        return true;
    }

    @TargetApi(21)
    public boolean isVideoSizeAndRateSupportedV21(int i, int i2, double d) {
        if (this.capabilities == null) {
            logNoSupport("sizeAndRate.caps");
            return false;
        }
        VideoCapabilities videoCapabilities = this.capabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("sizeAndRate.vCaps");
            return false;
        }
        if (!areSizeAndRateSupportedV21(videoCapabilities, i, i2, d)) {
            StringBuilder stringBuilder;
            if (i < i2) {
                if (areSizeAndRateSupportedV21(videoCapabilities, i2, i, d)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sizeAndRate.rotated, ");
                    stringBuilder.append(i);
                    stringBuilder.append("x");
                    stringBuilder.append(i2);
                    stringBuilder.append("x");
                    stringBuilder.append(d);
                    logAssumedSupport(stringBuilder.toString());
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("sizeAndRate.support, ");
            stringBuilder.append(i);
            stringBuilder.append("x");
            stringBuilder.append(i2);
            stringBuilder.append("x");
            stringBuilder.append(d);
            logNoSupport(stringBuilder.toString());
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public Point alignVideoSizeV21(int i, int i2) {
        if (this.capabilities == null) {
            logNoSupport("align.caps");
            return null;
        }
        VideoCapabilities videoCapabilities = this.capabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("align.vCaps");
            return null;
        }
        int widthAlignment = videoCapabilities.getWidthAlignment();
        int heightAlignment = videoCapabilities.getHeightAlignment();
        return new Point(Util.ceilDivide(i, widthAlignment) * widthAlignment, Util.ceilDivide(i2, heightAlignment) * heightAlignment);
    }

    @TargetApi(21)
    public boolean isAudioSampleRateSupportedV21(int i) {
        if (this.capabilities == null) {
            logNoSupport("sampleRate.caps");
            return false;
        }
        AudioCapabilities audioCapabilities = this.capabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("sampleRate.aCaps");
            return false;
        } else if (audioCapabilities.isSampleRateSupported(i)) {
            return true;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sampleRate.support, ");
            stringBuilder.append(i);
            logNoSupport(stringBuilder.toString());
            return false;
        }
    }

    @TargetApi(21)
    public boolean isAudioChannelCountSupportedV21(int i) {
        if (this.capabilities == null) {
            logNoSupport("channelCount.caps");
            return false;
        }
        AudioCapabilities audioCapabilities = this.capabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("channelCount.aCaps");
            return false;
        } else if (adjustMaxInputChannelCount(this.name, this.mimeType, audioCapabilities.getMaxInputChannelCount()) >= i) {
            return true;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("channelCount.support, ");
            stringBuilder.append(i);
            logNoSupport(stringBuilder.toString());
            return false;
        }
    }

    private void logNoSupport(String str) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NoSupport [");
        stringBuilder.append(str);
        stringBuilder.append("] [");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        stringBuilder.append(this.mimeType);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.d(str2, stringBuilder.toString());
    }

    private void logAssumedSupport(String str) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AssumedSupport [");
        stringBuilder.append(str);
        stringBuilder.append("] [");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        stringBuilder.append(this.mimeType);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.d(str2, stringBuilder.toString());
    }

    private static int adjustMaxInputChannelCount(String str, String str2, int i) {
        if (i <= 1) {
            if (Util.SDK_INT < 26 || i <= 0) {
                if (!(MimeTypes.AUDIO_MPEG.equals(str2) || MimeTypes.AUDIO_AMR_NB.equals(str2) || MimeTypes.AUDIO_AMR_WB.equals(str2) || MimeTypes.AUDIO_AAC.equals(str2) || MimeTypes.AUDIO_VORBIS.equals(str2) || MimeTypes.AUDIO_OPUS.equals(str2) || MimeTypes.AUDIO_RAW.equals(str2) || MimeTypes.AUDIO_FLAC.equals(str2) || MimeTypes.AUDIO_ALAW.equals(str2) || MimeTypes.AUDIO_MLAW.equals(str2))) {
                    if (!MimeTypes.AUDIO_MSGSM.equals(str2)) {
                        str2 = MimeTypes.AUDIO_AC3.equals(str2) ? 6 : MimeTypes.AUDIO_E_AC3.equals(str2) != null ? 16 : 30;
                        String str3 = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("AssumedMaxChannelAdjustment: ");
                        stringBuilder.append(str);
                        stringBuilder.append(", [");
                        stringBuilder.append(i);
                        stringBuilder.append(" to ");
                        stringBuilder.append(str2);
                        stringBuilder.append("]");
                        Log.w(str3, stringBuilder.toString());
                        return str2;
                    }
                }
                return i;
            }
        }
        return i;
    }

    private static boolean isAdaptive(CodecCapabilities codecCapabilities) {
        return (Util.SDK_INT < 19 || isAdaptiveV19(codecCapabilities) == null) ? null : true;
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities codecCapabilities) {
        return codecCapabilities.isFeatureSupported("adaptive-playback");
    }

    private static boolean isTunneling(CodecCapabilities codecCapabilities) {
        return (Util.SDK_INT < 21 || isTunnelingV21(codecCapabilities) == null) ? null : true;
    }

    @TargetApi(21)
    private static boolean isTunnelingV21(CodecCapabilities codecCapabilities) {
        return codecCapabilities.isFeatureSupported("tunneled-playback");
    }

    private static boolean isSecure(CodecCapabilities codecCapabilities) {
        return (Util.SDK_INT < 21 || isSecureV21(codecCapabilities) == null) ? null : true;
    }

    @TargetApi(21)
    private static boolean isSecureV21(CodecCapabilities codecCapabilities) {
        return codecCapabilities.isFeatureSupported("secure-playback");
    }

    @TargetApi(21)
    private static boolean areSizeAndRateSupportedV21(VideoCapabilities videoCapabilities, int i, int i2, double d) {
        if (d != -1.0d) {
            if (d > 0.0d) {
                return videoCapabilities.areSizeAndRateSupported(i, i2, d);
            }
        }
        return videoCapabilities.isSizeSupported(i, i2);
    }
}
