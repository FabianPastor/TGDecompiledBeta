package org.webrtc;
/* loaded from: classes.dex */
public interface SSLCertificateVerifier {
    @CalledByNative
    boolean verify(byte[] bArr);
}
