package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
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

    public static MediaCodecInfo newPassthroughInstance(String name) {
        return new MediaCodecInfo(name, null, null);
    }

    public static MediaCodecInfo newInstance(String name, String mimeType, CodecCapabilities capabilities) {
        return new MediaCodecInfo(name, mimeType, capabilities);
    }

    private MediaCodecInfo(String name, String mimeType, CodecCapabilities capabilities) {
        this.name = (String) Assertions.checkNotNull(name);
        this.mimeType = mimeType;
        this.capabilities = capabilities;
        boolean z = capabilities != null && isAdaptive(capabilities);
        this.adaptive = z;
    }

    public CodecProfileLevel[] getProfileLevels() {
        return (this.capabilities == null || this.capabilities.profileLevels == null) ? new CodecProfileLevel[0] : this.capabilities.profileLevels;
    }

    public boolean isCodecSupported(String codec) {
        if (codec == null || this.mimeType == null) {
            return true;
        }
        String codecMimeType = MimeTypes.getMediaMimeType(codec);
        if (codecMimeType == null) {
            return true;
        }
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
            logNoSupport("codec.profileLevel, " + codec + ", " + codecMimeType);
            return false;
        }
        logNoSupport("codec.mime " + codec + ", " + codecMimeType);
        return false;
    }

    @TargetApi(21)
    public boolean isVideoSizeSupportedV21(int width, int height) {
        if (this.capabilities == null) {
            logNoSupport("size.caps");
            return false;
        }
        VideoCapabilities videoCapabilities = this.capabilities.getVideoCapabilities();
        if (videoCapabilities == null) {
            logNoSupport("size.vCaps");
            return false;
        }
        if (!videoCapabilities.isSizeSupported(width, height)) {
            if (width >= height || !videoCapabilities.isSizeSupported(height, width)) {
                logNoSupport("size.support, " + width + "x" + height);
                return false;
            }
            logAssumedSupport("size.rotated, " + width + "x" + height);
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
        if (!videoCapabilities.areSizeAndRateSupported(width, height, frameRate)) {
            if (width >= height || !videoCapabilities.areSizeAndRateSupported(height, width, frameRate)) {
                logNoSupport("sizeAndRate.support, " + width + "x" + height + "x" + frameRate);
                return false;
            }
            logAssumedSupport("sizeAndRate.rotated, " + width + "x" + height + "x" + frameRate);
        }
        return true;
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
            logNoSupport("sampleRate.support, " + sampleRate);
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
        } else if (audioCapabilities.getMaxInputChannelCount() >= channelCount) {
            return true;
        } else {
            logNoSupport("channelCount.support, " + channelCount);
            return false;
        }
    }

    private void logNoSupport(String message) {
        Log.d(TAG, "NoSupport [" + message + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
    }

    private void logAssumedSupport(String message) {
        Log.d(TAG, "AssumedSupport [" + message + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
    }

    private static boolean isAdaptive(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 19 && isAdaptiveV19(capabilities);
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("adaptive-playback");
    }
}
