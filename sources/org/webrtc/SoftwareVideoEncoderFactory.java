package org.webrtc;

import java.util.ArrayList;
import java.util.HashMap;
import org.webrtc.VideoEncoderFactory;

public class SoftwareVideoEncoderFactory implements VideoEncoderFactory {
    @CalledByNative
    public /* synthetic */ VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return VideoEncoderFactory.CC.$default$getEncoderSelector(this);
    }

    @CalledByNative
    public /* synthetic */ VideoCodecInfo[] getImplementations() {
        return VideoEncoderFactory.CC.$default$getImplementations(this);
    }

    public VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo) {
        if (videoCodecInfo.name.equalsIgnoreCase("VP8")) {
            return new LibvpxVp8Encoder();
        }
        if (!videoCodecInfo.name.equalsIgnoreCase("VP9") || !LibvpxVp9Encoder.nativeIsSupported()) {
            return null;
        }
        return new LibvpxVp9Encoder();
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        return supportedCodecs();
    }

    static VideoCodecInfo[] supportedCodecs() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new VideoCodecInfo("VP8", new HashMap()));
        if (LibvpxVp9Encoder.nativeIsSupported()) {
            arrayList.add(new VideoCodecInfo("VP9", new HashMap()));
        }
        return (VideoCodecInfo[]) arrayList.toArray(new VideoCodecInfo[arrayList.size()]);
    }
}
