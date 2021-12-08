package org.webrtc;

public class LibvpxVp8Encoder extends WrappedNativeVideoEncoder {
    static native long nativeCreateEncoder();

    public boolean isHardwareEncoder() {
        return false;
    }

    public long createNativeVideoEncoder() {
        return nativeCreateEncoder();
    }
}
