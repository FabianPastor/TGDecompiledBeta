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

    public static MediaCodecInfo newPassthroughInstance(String name) {
        return new MediaCodecInfo(name, null, null, false, false);
    }

    public static MediaCodecInfo newInstance(String name, String mimeType, CodecCapabilities capabilities) {
        return new MediaCodecInfo(name, mimeType, capabilities, false, false);
    }

    public static MediaCodecInfo newInstance(String name, String mimeType, CodecCapabilities capabilities, boolean forceDisableAdaptive, boolean forceSecure) {
        return new MediaCodecInfo(name, mimeType, capabilities, forceDisableAdaptive, forceSecure);
    }

    private MediaCodecInfo(String name, String mimeType, CodecCapabilities capabilities, boolean forceDisableAdaptive, boolean forceSecure) {
        this.name = (String) Assertions.checkNotNull(name);
        this.mimeType = mimeType;
        this.capabilities = capabilities;
        boolean z = false;
        boolean z2 = (forceDisableAdaptive || capabilities == null || !isAdaptive(capabilities)) ? false : true;
        this.adaptive = z2;
        z2 = capabilities != null && isTunneling(capabilities);
        this.tunneling = z2;
        if (!forceSecure) {
            if (capabilities == null || !isSecure(capabilities)) {
                this.secure = z;
            }
        }
        z = true;
        this.secure = z;
    }

    public CodecProfileLevel[] getProfileLevels() {
        if (this.capabilities != null) {
            if (this.capabilities.profileLevels != null) {
                return this.capabilities.profileLevels;
            }
        }
        return new CodecProfileLevel[0];
    }

    public boolean isCodecSupported(String codec) {
        if (codec != null) {
            if (this.mimeType != null) {
                String codecMimeType = MimeTypes.getMediaMimeType(codec);
                if (codecMimeType == null) {
                    return true;
                }
                StringBuilder stringBuilder;
                if (this.mimeType.equals(codecMimeType)) {
                    Pair<Integer, Integer> codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(codec);
                    if (codecProfileAndLevel == null) {
                        return true;
                    }
                    for (CodecProfileLevel capabilities : getProfileLevels()) {
                        if (capabilities.profile == ((Integer) codecProfileAndLevel.first).intValue() && capabilities.level >= ((Integer) codecProfileAndLevel.second).intValue()) {
                            return true;
                        }
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("codec.profileLevel, ");
                    stringBuilder.append(codec);
                    stringBuilder.append(", ");
                    stringBuilder.append(codecMimeType);
                    logNoSupport(stringBuilder.toString());
                    return false;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("codec.mime ");
                stringBuilder.append(codec);
                stringBuilder.append(", ");
                stringBuilder.append(codecMimeType);
                logNoSupport(stringBuilder.toString());
                return false;
            }
        }
        return true;
    }

    @TargetApi(21)
    public boolean isVideoSizeAndRateSupportedV21(int width, int height, double frameRate) {
        if (this.capabilities == null) {
            logNoSupport("sizeAndRate.caps");
            return false;
        }
        VideoCapabilities videoCapabilities = this.capabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("sizeAndRate.vCaps");
            return false;
        }
        if (!areSizeAndRateSupportedV21(videoCapabilities, width, height, frameRate)) {
            if (width < height) {
                if (areSizeAndRateSupportedV21(videoCapabilities, height, width, frameRate)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("sizeAndRate.rotated, ");
                    stringBuilder.append(width);
                    stringBuilder.append("x");
                    stringBuilder.append(height);
                    stringBuilder.append("x");
                    stringBuilder.append(frameRate);
                    logAssumedSupport(stringBuilder.toString());
                }
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("sizeAndRate.support, ");
            stringBuilder2.append(width);
            stringBuilder2.append("x");
            stringBuilder2.append(height);
            stringBuilder2.append("x");
            stringBuilder2.append(frameRate);
            logNoSupport(stringBuilder2.toString());
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public Point alignVideoSizeV21(int width, int height) {
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
        return new Point(Util.ceilDivide(width, widthAlignment) * widthAlignment, Util.ceilDivide(height, heightAlignment) * heightAlignment);
    }

    @TargetApi(21)
    public boolean isAudioSampleRateSupportedV21(int sampleRate) {
        if (this.capabilities == null) {
            logNoSupport("sampleRate.caps");
            return false;
        }
        AudioCapabilities audioCapabilities = this.capabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("sampleRate.aCaps");
            return false;
        } else if (audioCapabilities.isSampleRateSupported(sampleRate)) {
            return true;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sampleRate.support, ");
            stringBuilder.append(sampleRate);
            logNoSupport(stringBuilder.toString());
            return false;
        }
    }

    @TargetApi(21)
    public boolean isAudioChannelCountSupportedV21(int channelCount) {
        if (this.capabilities == null) {
            logNoSupport("channelCount.caps");
            return false;
        }
        AudioCapabilities audioCapabilities = this.capabilities.getAudioCapabilities();
        if (audioCapabilities == null) {
            logNoSupport("channelCount.aCaps");
            return false;
        } else if (adjustMaxInputChannelCount(this.name, this.mimeType, audioCapabilities.getMaxInputChannelCount()) >= channelCount) {
            return true;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("channelCount.support, ");
            stringBuilder.append(channelCount);
            logNoSupport(stringBuilder.toString());
            return false;
        }
    }

    private void logNoSupport(String message) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NoSupport [");
        stringBuilder.append(message);
        stringBuilder.append("] [");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        stringBuilder.append(this.mimeType);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    private void logAssumedSupport(String message) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AssumedSupport [");
        stringBuilder.append(message);
        stringBuilder.append("] [");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        stringBuilder.append(this.mimeType);
        stringBuilder.append("] [");
        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    private static int adjustMaxInputChannelCount(String name, String mimeType, int maxChannelCount) {
        if (maxChannelCount <= 1) {
            if (Util.SDK_INT < 26 || maxChannelCount <= 0) {
                if (!(MimeTypes.AUDIO_MPEG.equals(mimeType) || MimeTypes.AUDIO_AMR_NB.equals(mimeType) || MimeTypes.AUDIO_AMR_WB.equals(mimeType) || MimeTypes.AUDIO_AAC.equals(mimeType) || MimeTypes.AUDIO_VORBIS.equals(mimeType) || MimeTypes.AUDIO_OPUS.equals(mimeType) || MimeTypes.AUDIO_RAW.equals(mimeType) || MimeTypes.AUDIO_FLAC.equals(mimeType) || MimeTypes.AUDIO_ALAW.equals(mimeType) || MimeTypes.AUDIO_MLAW.equals(mimeType))) {
                    if (!MimeTypes.AUDIO_MSGSM.equals(mimeType)) {
                        int assumedMaxChannelCount;
                        String str;
                        StringBuilder stringBuilder;
                        if (MimeTypes.AUDIO_AC3.equals(mimeType)) {
                            assumedMaxChannelCount = 6;
                        } else if (MimeTypes.AUDIO_E_AC3.equals(mimeType)) {
                            assumedMaxChannelCount = 16;
                        } else {
                            assumedMaxChannelCount = 30;
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("AssumedMaxChannelAdjustment: ");
                            stringBuilder.append(name);
                            stringBuilder.append(", [");
                            stringBuilder.append(maxChannelCount);
                            stringBuilder.append(" to ");
                            stringBuilder.append(assumedMaxChannelCount);
                            stringBuilder.append("]");
                            Log.w(str, stringBuilder.toString());
                            return assumedMaxChannelCount;
                        }
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("AssumedMaxChannelAdjustment: ");
                        stringBuilder.append(name);
                        stringBuilder.append(", [");
                        stringBuilder.append(maxChannelCount);
                        stringBuilder.append(" to ");
                        stringBuilder.append(assumedMaxChannelCount);
                        stringBuilder.append("]");
                        Log.w(str, stringBuilder.toString());
                        return assumedMaxChannelCount;
                    }
                }
                return maxChannelCount;
            }
        }
        return maxChannelCount;
    }

    private static boolean isAdaptive(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 19 && isAdaptiveV19(capabilities);
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("adaptive-playback");
    }

    private static boolean isTunneling(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 21 && isTunnelingV21(capabilities);
    }

    @TargetApi(21)
    private static boolean isTunnelingV21(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("tunneled-playback");
    }

    private static boolean isSecure(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 21 && isSecureV21(capabilities);
    }

    @TargetApi(21)
    private static boolean isSecureV21(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("secure-playback");
    }

    @TargetApi(21)
    private static boolean areSizeAndRateSupportedV21(VideoCapabilities capabilities, int width, int height, double frameRate) {
        if (frameRate != -1.0d) {
            if (frameRate > 0.0d) {
                return capabilities.areSizeAndRateSupported(width, height, frameRate);
            }
        }
        return capabilities.isSizeSupported(width, height);
    }
}
