package org.webrtc;

import org.webrtc.EncodedImage;

public interface VideoEncoder {

    public interface Callback {
        void onEncodedFrame(EncodedImage encodedImage, CodecSpecificInfo codecSpecificInfo);
    }

    public static class CodecSpecificInfo {
    }

    public static class CodecSpecificInfoAV1 extends CodecSpecificInfo {
    }

    public static class CodecSpecificInfoH264 extends CodecSpecificInfo {
    }

    public static class CodecSpecificInfoVP8 extends CodecSpecificInfo {
    }

    public static class CodecSpecificInfoVP9 extends CodecSpecificInfo {
    }

    long createNativeVideoEncoder();

    VideoCodecStatus encode(VideoFrame videoFrame, EncodeInfo encodeInfo);

    EncoderInfo getEncoderInfo();

    String getImplementationName();

    ResolutionBitrateLimits[] getResolutionBitrateLimits();

    ScalingSettings getScalingSettings();

    VideoCodecStatus initEncode(Settings settings, Callback callback);

    boolean isHardwareEncoder();

    VideoCodecStatus release();

    VideoCodecStatus setRateAllocation(BitrateAllocation bitrateAllocation, int i);

    VideoCodecStatus setRates(RateControlParameters rateControlParameters);

    public static class Settings {
        public final boolean automaticResizeOn;
        public final Capabilities capabilities;
        public final int height;
        public final int maxFramerate;
        public final int numberOfCores;
        public final int numberOfSimulcastStreams;
        public final int startBitrate;
        public final int width;

        @Deprecated
        public Settings(int numberOfCores2, int width2, int height2, int startBitrate2, int maxFramerate2, int numberOfSimulcastStreams2, boolean automaticResizeOn2) {
            this(numberOfCores2, width2, height2, startBitrate2, maxFramerate2, numberOfSimulcastStreams2, automaticResizeOn2, new Capabilities(false));
        }

        public Settings(int numberOfCores2, int width2, int height2, int startBitrate2, int maxFramerate2, int numberOfSimulcastStreams2, boolean automaticResizeOn2, Capabilities capabilities2) {
            this.numberOfCores = numberOfCores2;
            this.width = width2;
            this.height = height2;
            this.startBitrate = startBitrate2;
            this.maxFramerate = maxFramerate2;
            this.numberOfSimulcastStreams = numberOfSimulcastStreams2;
            this.automaticResizeOn = automaticResizeOn2;
            this.capabilities = capabilities2;
        }
    }

    public static class Capabilities {
        public final boolean lossNotification;

        public Capabilities(boolean lossNotification2) {
            this.lossNotification = lossNotification2;
        }
    }

    public static class EncodeInfo {
        public final EncodedImage.FrameType[] frameTypes;

        public EncodeInfo(EncodedImage.FrameType[] frameTypes2) {
            this.frameTypes = frameTypes2;
        }
    }

    public static class BitrateAllocation {
        public final int[][] bitratesBbs;

        public BitrateAllocation(int[][] bitratesBbs2) {
            this.bitratesBbs = bitratesBbs2;
        }

        public int getSum() {
            int sum = 0;
            for (int[] spatialLayer : this.bitratesBbs) {
                for (int bitrate : r1[r4]) {
                    sum += bitrate;
                }
            }
            return sum;
        }
    }

    public static class ScalingSettings {
        public static final ScalingSettings OFF = new ScalingSettings();
        public final Integer high;
        public final Integer low;
        public final boolean on;

        public ScalingSettings(int low2, int high2) {
            this.on = true;
            this.low = Integer.valueOf(low2);
            this.high = Integer.valueOf(high2);
        }

        private ScalingSettings() {
            this.on = false;
            this.low = null;
            this.high = null;
        }

        @Deprecated
        public ScalingSettings(boolean on2) {
            this.on = on2;
            this.low = null;
            this.high = null;
        }

        @Deprecated
        public ScalingSettings(boolean on2, int low2, int high2) {
            this.on = on2;
            this.low = Integer.valueOf(low2);
            this.high = Integer.valueOf(high2);
        }

        public String toString() {
            if (!this.on) {
                return "OFF";
            }
            return "[ " + this.low + ", " + this.high + " ]";
        }
    }

    public static class ResolutionBitrateLimits {
        public final int frameSizePixels;
        public final int maxBitrateBps;
        public final int minBitrateBps;
        public final int minStartBitrateBps;

        public ResolutionBitrateLimits(int frameSizePixels2, int minStartBitrateBps2, int minBitrateBps2, int maxBitrateBps2) {
            this.frameSizePixels = frameSizePixels2;
            this.minStartBitrateBps = minStartBitrateBps2;
            this.minBitrateBps = minBitrateBps2;
            this.maxBitrateBps = maxBitrateBps2;
        }

        public int getFrameSizePixels() {
            return this.frameSizePixels;
        }

        public int getMinStartBitrateBps() {
            return this.minStartBitrateBps;
        }

        public int getMinBitrateBps() {
            return this.minBitrateBps;
        }

        public int getMaxBitrateBps() {
            return this.maxBitrateBps;
        }
    }

    public static class RateControlParameters {
        public final BitrateAllocation bitrate;
        public final double framerateFps;

        public RateControlParameters(BitrateAllocation bitrate2, double framerateFps2) {
            this.bitrate = bitrate2;
            this.framerateFps = framerateFps2;
        }
    }

    public static class EncoderInfo {
        public final boolean applyAlignmentToAllSimulcastLayers;
        public final int requestedResolutionAlignment;

        public EncoderInfo(int requestedResolutionAlignment2, boolean applyAlignmentToAllSimulcastLayers2) {
            this.requestedResolutionAlignment = requestedResolutionAlignment2;
            this.applyAlignmentToAllSimulcastLayers = applyAlignmentToAllSimulcastLayers2;
        }

        public int getRequestedResolutionAlignment() {
            return this.requestedResolutionAlignment;
        }

        public boolean getApplyAlignmentToAllSimulcastLayers() {
            return this.applyAlignmentToAllSimulcastLayers;
        }
    }

    /* renamed from: org.webrtc.VideoEncoder$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static long $default$createNativeVideoEncoder(VideoEncoder _this) {
            return 0;
        }

        public static boolean $default$isHardwareEncoder(VideoEncoder _this) {
            return true;
        }

        public static VideoCodecStatus $default$setRates(VideoEncoder _this, RateControlParameters rcParameters) {
            return _this.setRateAllocation(rcParameters.bitrate, (int) Math.ceil(rcParameters.framerateFps));
        }

        public static ResolutionBitrateLimits[] $default$getResolutionBitrateLimits(VideoEncoder _this) {
            return new ResolutionBitrateLimits[0];
        }

        public static EncoderInfo $default$getEncoderInfo(VideoEncoder _this) {
            return new EncoderInfo(1, false);
        }
    }
}
