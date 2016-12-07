package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo.CodecCapabilities;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(16)
public final class DecoderInfo {
    public final boolean adaptive;
    public final CodecCapabilities capabilities;
    public final String name;

    DecoderInfo(String name, CodecCapabilities capabilities) {
        this.name = name;
        this.capabilities = capabilities;
        this.adaptive = isAdaptive(capabilities);
    }

    private static boolean isAdaptive(CodecCapabilities capabilities) {
        return capabilities != null && Util.SDK_INT >= 19 && isAdaptiveV19(capabilities);
    }

    @TargetApi(19)
    private static boolean isAdaptiveV19(CodecCapabilities capabilities) {
        return capabilities.isFeatureSupported("adaptive-playback");
    }
}
