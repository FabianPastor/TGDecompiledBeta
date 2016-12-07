package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.util.Pair;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo {
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
        if (!this.mimeType.equals(codecMimeType)) {
            return false;
        }
        Pair<Integer, Integer> codecProfileAndLevel = MediaCodecUtil.getCodecProfileAndLevel(codec);
        if (codecProfileAndLevel == null) {
            return true;
        }
        for (CodecProfileLevel capabilities : getProfileLevels()) {
            if (capabilities.profile == ((Integer) codecProfileAndLevel.first).intValue() && capabilities.level >= ((Integer) codecProfileAndLevel.second).intValue()) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(21)
    public boolean isVideoSizeSupportedV21(int width, int height) {
        if (this.capabilities == null) {
            return false;
        }
        VideoCapabilities videoCapabilities = this.capabilities.getVideoCapabilities();
        if (videoCapabilities == null || !videoCapabilities.isSizeSupported(width, height)) {
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public boolean isVideoSizeAndRateSupportedV21(int width, int height, double frameRate) {
        if (this.capabilities == null) {
            return false;
        }
        VideoCapabilities videoCapabilities = this.capabilities.getVideoCapabilities();
        if (videoCapabilities == null || !videoCapabilities.areSizeAndRateSupported(width, height, frameRate)) {
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public boolean isAudioSampleRateSupportedV21(int sampleRate) {
        if (this.capabilities == null) {
            return false;
        }
        AudioCapabilities audioCapabilities = this.capabilities.getAudioCapabilities();
        if (audioCapabilities == null || !audioCapabilities.isSampleRateSupported(sampleRate)) {
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public boolean isAudioChannelCountSupportedV21(int channelCount) {
        if (this.capabilities == null) {
            return false;
        }
        AudioCapabilities audioCapabilities = this.capabilities.getAudioCapabilities();
        if (audioCapabilities == null || audioCapabilities.getMaxInputChannelCount() < channelCount) {
            return false;
        }
        return true;
    }

    private static boolean isAdaptive(CodecCapabilities capabilities) {
        return Util.SDK_INT >= 19 && isAdaptiveV19(capabilities);
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("adaptive-playback");
    }
}
