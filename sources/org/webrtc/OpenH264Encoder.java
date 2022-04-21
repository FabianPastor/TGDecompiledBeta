package org.webrtc;

public class OpenH264Encoder extends WrappedNativeVideoEncoder {
    static native long nativeCreateEncoder();

    public long createNativeVideoEncoder() {
        return nativeCreateEncoder();
    }

    public boolean isHardwareEncoder() {
        return false;
    }
}
