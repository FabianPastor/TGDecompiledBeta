package org.webrtc;

public class LibvpxVp9Decoder extends WrappedNativeVideoDecoder {
    static native long nativeCreateDecoder();

    static native boolean nativeIsSupported();

    public long createNativeVideoDecoder() {
        return nativeCreateDecoder();
    }
}
