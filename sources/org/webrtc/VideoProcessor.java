package org.webrtc;

public interface VideoProcessor extends CapturerObserver {
    void onFrameCaptured(VideoFrame videoFrame, FrameAdaptationParameters frameAdaptationParameters);

    void setSink(VideoSink videoSink);

    public static class FrameAdaptationParameters {
        public final int cropHeight;
        public final int cropWidth;
        public final int cropX;
        public final int cropY;
        public final boolean drop;
        public final int scaleHeight;
        public final int scaleWidth;
        public final long timestampNs;

        public FrameAdaptationParameters(int cropX2, int cropY2, int cropWidth2, int cropHeight2, int scaleWidth2, int scaleHeight2, long timestampNs2, boolean drop2) {
            this.cropX = cropX2;
            this.cropY = cropY2;
            this.cropWidth = cropWidth2;
            this.cropHeight = cropHeight2;
            this.scaleWidth = scaleWidth2;
            this.scaleHeight = scaleHeight2;
            this.timestampNs = timestampNs2;
            this.drop = drop2;
        }
    }

    /* renamed from: org.webrtc.VideoProcessor$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onFrameCaptured(VideoProcessor _this, VideoFrame frame, FrameAdaptationParameters parameters) {
            VideoFrame adaptedFrame = applyFrameAdaptationParameters(frame, parameters);
            if (adaptedFrame != null) {
                _this.onFrameCaptured(adaptedFrame);
                adaptedFrame.release();
            }
        }

        public static VideoFrame applyFrameAdaptationParameters(VideoFrame frame, FrameAdaptationParameters parameters) {
            if (parameters.drop) {
                return null;
            }
            return new VideoFrame(frame.getBuffer().cropAndScale(parameters.cropX, parameters.cropY, parameters.cropWidth, parameters.cropHeight, parameters.scaleWidth, parameters.scaleHeight), frame.getRotation(), parameters.timestampNs);
        }
    }
}
