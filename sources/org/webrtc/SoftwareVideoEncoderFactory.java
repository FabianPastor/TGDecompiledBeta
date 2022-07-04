package org.webrtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.webrtc.VideoEncoderFactory;

public class SoftwareVideoEncoderFactory implements VideoEncoderFactory {
    public /* synthetic */ VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return VideoEncoderFactory.CC.$default$getEncoderSelector(this);
    }

    public /* synthetic */ VideoCodecInfo[] getImplementations() {
        return VideoEncoderFactory.CC.$default$getImplementations(this);
    }

    public VideoEncoder createEncoder(VideoCodecInfo info) {
        if (info.name.equalsIgnoreCase("VP8")) {
            return new LibvpxVp8Encoder();
        }
        if (info.name.equalsIgnoreCase("VP9") && LibvpxVp9Encoder.nativeIsSupported()) {
            return new LibvpxVp9Encoder();
        }
        if (info.name.equalsIgnoreCase("H264")) {
            return new OpenH264Encoder();
        }
        return null;
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        return supportedCodecs();
    }

    static VideoCodecInfo[] supportedCodecs() {
        List<VideoCodecInfo> codecs = new ArrayList<>();
        codecs.add(new VideoCodecInfo("VP8", new HashMap()));
        codecs.add(new VideoCodecInfo("H264", new HashMap()));
        if (LibvpxVp9Encoder.nativeIsSupported()) {
            codecs.add(new VideoCodecInfo("VP9", new HashMap()));
        }
        return (VideoCodecInfo[]) codecs.toArray(new VideoCodecInfo[codecs.size()]);
    }
}
