package org.webrtc;
/* loaded from: classes.dex */
class WebRtcClassLoader {
    WebRtcClassLoader() {
    }

    @CalledByNative
    static Object getClassLoader() {
        ClassLoader classLoader = WebRtcClassLoader.class.getClassLoader();
        if (classLoader != null) {
            return classLoader;
        }
        throw new RuntimeException("Failed to get WebRTC class loader.");
    }
}
