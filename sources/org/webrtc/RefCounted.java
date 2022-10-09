package org.webrtc;
/* loaded from: classes3.dex */
public interface RefCounted {
    @CalledByNative
    void release();

    @CalledByNative
    void retain();
}
