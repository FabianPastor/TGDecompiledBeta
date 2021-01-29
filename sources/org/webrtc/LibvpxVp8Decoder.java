package org.webrtc;

public class LibvpxVp8Decoder extends WrappedNativeVideoDecoder {
    static native long nativeCreateDecoder();

    public long createNativeVideoDecoder() {
        return nativeCreateDecoder();
    }
}
