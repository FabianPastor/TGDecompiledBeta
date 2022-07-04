package org.webrtc;

import java.util.Arrays;
import java.util.LinkedHashSet;
import org.webrtc.EglBase;
import org.webrtc.VideoDecoderFactory;

public class DefaultVideoDecoderFactory implements VideoDecoderFactory {
    private final VideoDecoderFactory hardwareVideoDecoderFactory;
    private final VideoDecoderFactory platformSoftwareVideoDecoderFactory;
    private final VideoDecoderFactory softwareVideoDecoderFactory = new SoftwareVideoDecoderFactory();

    public /* synthetic */ VideoDecoder createDecoder(String str) {
        return VideoDecoderFactory.CC.$default$createDecoder((VideoDecoderFactory) this, str);
    }

    public DefaultVideoDecoderFactory(EglBase.Context eglContext) {
        this.hardwareVideoDecoderFactory = new HardwareVideoDecoderFactory(eglContext);
        this.platformSoftwareVideoDecoderFactory = new PlatformSoftwareVideoDecoderFactory(eglContext);
    }

    DefaultVideoDecoderFactory(VideoDecoderFactory hardwareVideoDecoderFactory2) {
        this.hardwareVideoDecoderFactory = hardwareVideoDecoderFactory2;
        this.platformSoftwareVideoDecoderFactory = null;
    }

    public VideoDecoder createDecoder(VideoCodecInfo codecType) {
        VideoDecoderFactory videoDecoderFactory;
        VideoDecoder softwareDecoder = this.softwareVideoDecoderFactory.createDecoder(codecType);
        VideoDecoder hardwareDecoder = this.hardwareVideoDecoderFactory.createDecoder(codecType);
        if (softwareDecoder == null && (videoDecoderFactory = this.platformSoftwareVideoDecoderFactory) != null) {
            softwareDecoder = videoDecoderFactory.createDecoder(codecType);
        }
        if (hardwareDecoder == null || softwareDecoder == null) {
            return hardwareDecoder != null ? hardwareDecoder : softwareDecoder;
        }
        return new VideoDecoderFallback(softwareDecoder, hardwareDecoder);
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet<>();
        supportedCodecInfos.addAll(Arrays.asList(this.softwareVideoDecoderFactory.getSupportedCodecs()));
        supportedCodecInfos.addAll(Arrays.asList(this.hardwareVideoDecoderFactory.getSupportedCodecs()));
        VideoDecoderFactory videoDecoderFactory = this.platformSoftwareVideoDecoderFactory;
        if (videoDecoderFactory != null) {
            supportedCodecInfos.addAll(Arrays.asList(videoDecoderFactory.getSupportedCodecs()));
        }
        return (VideoCodecInfo[]) supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
    }
}
