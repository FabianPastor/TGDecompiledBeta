package org.webrtc;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
    private static final String TAG = "Metrics";
    public final Map<String, HistogramInfo> map = new HashMap();

    private static native void nativeEnable();

    private static native Metrics nativeGetAndReset();

    Metrics() {
    }

    public static class HistogramInfo {
        public final int bucketCount;
        public final int max;
        public final int min;
        public final Map<Integer, Integer> samples = new HashMap();

        public HistogramInfo(int min2, int max2, int bucketCount2) {
            this.min = min2;
            this.max = max2;
            this.bucketCount = bucketCount2;
        }

        public void addSample(int value, int numEvents) {
            this.samples.put(Integer.valueOf(value), Integer.valueOf(numEvents));
        }
    }

    private void add(String name, HistogramInfo info) {
        this.map.put(name, info);
    }

    public static void enable() {
        nativeEnable();
    }

    public static Metrics getAndReset() {
        return nativeGetAndReset();
    }
}
