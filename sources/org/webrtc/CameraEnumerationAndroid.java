package org.webrtc;

import android.graphics.ImageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraEnumerationAndroid {
    static final ArrayList<Size> COMMON_RESOLUTIONS = new ArrayList<>(Arrays.asList(new Size[]{new Size(160, 120), new Size(240, 160), new Size(320, 240), new Size(400, 240), new Size(480, 320), new Size(640, 360), new Size(640, 480), new Size(768, 480), new Size(854, 480), new Size(800, 600), new Size(960, 540), new Size(960, 640), new Size(1024, 576), new Size(1024, 600), new Size(1280, 720), new Size(1280, 1024), new Size(1920, 1080), new Size(1920, 1440), new Size(2560, 1440), new Size(3840, 2160)}));
    private static final String TAG = "CameraEnumerationAndroid";

    public static class CaptureFormat {
        public final FramerateRange framerate;
        public final int height;
        public final int imageFormat = 17;
        public final int width;

        public static class FramerateRange {
            public int max;
            public int min;

            public FramerateRange(int min2, int max2) {
                this.min = min2;
                this.max = max2;
            }

            public String toString() {
                return "[" + (((float) this.min) / 1000.0f) + ":" + (((float) this.max) / 1000.0f) + "]";
            }

            public boolean equals(Object other) {
                if (!(other instanceof FramerateRange)) {
                    return false;
                }
                FramerateRange otherFramerate = (FramerateRange) other;
                if (this.min == otherFramerate.min && this.max == otherFramerate.max) {
                    return true;
                }
                return false;
            }

            public int hashCode() {
                return (this.min * 65537) + 1 + this.max;
            }
        }

        public CaptureFormat(int width2, int height2, int minFramerate, int maxFramerate) {
            this.width = width2;
            this.height = height2;
            this.framerate = new FramerateRange(minFramerate, maxFramerate);
        }

        public CaptureFormat(int width2, int height2, FramerateRange framerate2) {
            this.width = width2;
            this.height = height2;
            this.framerate = framerate2;
        }

        public int frameSize() {
            return frameSize(this.width, this.height, 17);
        }

        public static int frameSize(int width2, int height2, int imageFormat2) {
            if (imageFormat2 == 17) {
                return ((width2 * height2) * ImageFormat.getBitsPerPixel(imageFormat2)) / 8;
            }
            throw new UnsupportedOperationException("Don't know how to calculate the frame size of non-NV21 image formats.");
        }

        public String toString() {
            return this.width + "x" + this.height + "@" + this.framerate;
        }

        public boolean equals(Object other) {
            if (!(other instanceof CaptureFormat)) {
                return false;
            }
            CaptureFormat otherFormat = (CaptureFormat) other;
            if (this.width == otherFormat.width && this.height == otherFormat.height && this.framerate.equals(otherFormat.framerate)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((this.width * 65497) + this.height) * 251) + 1 + this.framerate.hashCode();
        }
    }

    private static abstract class ClosestComparator<T> implements Comparator<T> {
        /* access modifiers changed from: package-private */
        public abstract int diff(T t);

        private ClosestComparator() {
        }

        public int compare(T t1, T t2) {
            return diff(t1) - diff(t2);
        }
    }

    public static CaptureFormat.FramerateRange getClosestSupportedFramerateRange(List<CaptureFormat.FramerateRange> supportedFramerates, final int requestedFps) {
        return (CaptureFormat.FramerateRange) Collections.min(supportedFramerates, new ClosestComparator<CaptureFormat.FramerateRange>() {
            private static final int MAX_FPS_DIFF_THRESHOLD = 5000;
            private static final int MAX_FPS_HIGH_DIFF_WEIGHT = 3;
            private static final int MAX_FPS_LOW_DIFF_WEIGHT = 1;
            private static final int MIN_FPS_HIGH_VALUE_WEIGHT = 4;
            private static final int MIN_FPS_LOW_VALUE_WEIGHT = 1;
            private static final int MIN_FPS_THRESHOLD = 8000;

            private int progressivePenalty(int value, int threshold, int lowWeight, int highWeight) {
                if (value < threshold) {
                    return value * lowWeight;
                }
                return (threshold * lowWeight) + ((value - threshold) * highWeight);
            }

            /* access modifiers changed from: package-private */
            public int diff(CaptureFormat.FramerateRange range) {
                return progressivePenalty(range.min, 8000, 1, 4) + progressivePenalty(Math.abs((requestedFps * 1000) - range.max), 5000, 1, 3);
            }
        });
    }

    public static Size getClosestSupportedSize(List<Size> supportedSizes, final int requestedWidth, final int requestedHeight) {
        return (Size) Collections.min(supportedSizes, new ClosestComparator<Size>() {
            /* access modifiers changed from: package-private */
            public int diff(Size size) {
                return Math.abs(requestedWidth - size.width) + Math.abs(requestedHeight - size.height);
            }
        });
    }

    static void reportCameraResolution(Histogram histogram, Size resolution) {
        histogram.addSample(COMMON_RESOLUTIONS.indexOf(resolution) + 1);
    }
}
