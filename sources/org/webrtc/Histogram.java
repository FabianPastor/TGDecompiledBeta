package org.webrtc;

class Histogram {
    private final long handle;

    private static native void nativeAddSample(long j, int i);

    private static native long nativeCreateCounts(String str, int i, int i2, int i3);

    private static native long nativeCreateEnumeration(String str, int i);

    private Histogram(long handle2) {
        this.handle = handle2;
    }

    public static Histogram createCounts(String name, int min, int max, int bucketCount) {
        return new Histogram(nativeCreateCounts(name, min, max, bucketCount));
    }

    public static Histogram createEnumeration(String name, int max) {
        return new Histogram(nativeCreateEnumeration(name, max));
    }

    public void addSample(int sample) {
        nativeAddSample(this.handle, sample);
    }
}
