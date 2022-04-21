package org.webrtc;

public class LibvpxVp8Encoder extends WrappedNativeVideoEncoder {
    static native long nativeCreateEncoder();

    public long createNativeVideoEncoder() {
        return nativeCreateEncoder();
    }

    public boolean isHardwareEncoder() {
        return false;
    }
}
