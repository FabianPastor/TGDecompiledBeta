package org.webrtc;

public class LibvpxVp9Encoder extends WrappedNativeVideoEncoder {
    static native long nativeCreateEncoder();

    static native boolean nativeIsSupported();

    public long createNativeVideoEncoder() {
        return nativeCreateEncoder();
    }

    public boolean isHardwareEncoder() {
        return false;
    }
}
