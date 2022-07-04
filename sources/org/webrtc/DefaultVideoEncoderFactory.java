package org.webrtc;

import java.util.Arrays;
import java.util.LinkedHashSet;
import org.webrtc.EglBase;
import org.webrtc.VideoEncoderFactory;

public class DefaultVideoEncoderFactory implements VideoEncoderFactory {
    private final VideoEncoderFactory hardwareVideoEncoderFactory;
    private final VideoEncoderFactory softwareVideoEncoderFactory = new SoftwareVideoEncoderFactory();

    public /* synthetic */ VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return VideoEncoderFactory.CC.$default$getEncoderSelector(this);
    }

    public /* synthetic */ VideoCodecInfo[] getImplementations() {
        return VideoEncoderFactory.CC.$default$getImplementations(this);
    }

    public DefaultVideoEncoderFactory(EglBase.Context eglContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
        this.hardwareVideoEncoderFactory = new HardwareVideoEncoderFactory(eglContext, enableIntelVp8Encoder, enableH264HighProfile);
    }

    DefaultVideoEncoderFactory(VideoEncoderFactory hardwareVideoEncoderFactory2) {
        this.hardwareVideoEncoderFactory = hardwareVideoEncoderFactory2;
    }

    public VideoEncoder createEncoder(VideoCodecInfo info) {
        VideoEncoder softwareEncoder = this.softwareVideoEncoderFactory.createEncoder(info);
        VideoEncoder hardwareEncoder = this.hardwareVideoEncoderFactory.createEncoder(info);
        if (hardwareEncoder == null || softwareEncoder == null) {
            return hardwareEncoder != null ? hardwareEncoder : softwareEncoder;
        }
        return new VideoEncoderFallback(softwareEncoder, hardwareEncoder);
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet<>();
        supportedCodecInfos.addAll(Arrays.asList(this.softwareVideoEncoderFactory.getSupportedCodecs()));
        supportedCodecInfos.addAll(Arrays.asList(this.hardwareVideoEncoderFactory.getSupportedCodecs()));
        return (VideoCodecInfo[]) supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
    }
}
