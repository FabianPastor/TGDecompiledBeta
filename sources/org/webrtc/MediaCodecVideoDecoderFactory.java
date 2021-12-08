package org.webrtc;

import android.media.MediaCodecInfo;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;
import org.webrtc.EglBase;
import org.webrtc.VideoDecoderFactory;

class MediaCodecVideoDecoderFactory implements VideoDecoderFactory {
    private static final String TAG = "MediaCodecVideoDecoderFactory";
    private final Predicate<MediaCodecInfo> codecAllowedPredicate;
    private final EglBase.Context sharedContext;

    public /* synthetic */ VideoDecoder createDecoder(String str) {
        return VideoDecoderFactory.CC.$default$createDecoder((VideoDecoderFactory) this, str);
    }

    public MediaCodecVideoDecoderFactory(EglBase.Context sharedContext2, Predicate<MediaCodecInfo> codecAllowedPredicate2) {
        this.sharedContext = sharedContext2;
        this.codecAllowedPredicate = codecAllowedPredicate2;
    }

    public VideoDecoder createDecoder(VideoCodecInfo codecType) {
        VideoCodecMimeType type = VideoCodecMimeType.valueOf(codecType.getName());
        MediaCodecInfo info = findCodecForType(type);
        if (info == null) {
            return null;
        }
        return new AndroidVideoDecoder(new MediaCodecWrapperFactoryImpl(), info.getName(), type, MediaCodecUtils.selectColorFormat(MediaCodecUtils.DECODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())).intValue(), this.sharedContext);
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        List<VideoCodecInfo> supportedCodecInfos = new ArrayList<>();
        VideoCodecMimeType[] videoCodecMimeTypeArr = {VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264, VideoCodecMimeType.H265};
        for (int i = 0; i < 4; i++) {
            VideoCodecMimeType type = videoCodecMimeTypeArr[i];
            MediaCodecInfo codec = findCodecForType(type);
            if (codec != null) {
                String name = type.name();
                if (type == VideoCodecMimeType.H264 && isH264HighProfileSupported(codec)) {
                    supportedCodecInfos.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(type, true)));
                }
                supportedCodecInfos.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(type, false)));
            }
        }
        return (VideoCodecInfo[]) supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
    }

    private MediaCodecInfo findCodecForType(VideoCodecMimeType type) {
        if (Build.VERSION.SDK_INT < 19) {
            return null;
        }
        ArrayList<MediaCodecInfo> infos = MediaCodecUtils.getSortedCodecsList();
        int codecCount = infos.size();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo info = infos.get(i);
            if (info != null && !info.isEncoder() && isSupportedCodec(info, type)) {
                return info;
            }
        }
        return null;
    }

    private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecMimeType type) {
        String name = info.getName();
        if (MediaCodecUtils.codecSupportsType(info, type) && MediaCodecUtils.selectColorFormat(MediaCodecUtils.DECODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())) != null) {
            return isCodecAllowed(info);
        }
        return false;
    }

    private boolean isCodecAllowed(MediaCodecInfo info) {
        Predicate<MediaCodecInfo> predicate = this.codecAllowedPredicate;
        if (predicate == null) {
            return true;
        }
        return predicate.test(info);
    }

    private boolean isH264HighProfileSupported(MediaCodecInfo info) {
        String name = info.getName();
        if (Build.VERSION.SDK_INT >= 21 && name.startsWith("OMX.qcom.")) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 23 || !name.startsWith("OMX.Exynos.")) {
            return false;
        }
        return true;
    }
}
