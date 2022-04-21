package org.webrtc;

import org.webrtc.Logging;

public class CallSessionFileRotatingLogSink {
    private long nativeSink;

    private static native long nativeAddSink(String str, int i, int i2);

    private static native void nativeDeleteSink(long j);

    private static native byte[] nativeGetLogData(String str);

    public static byte[] getLogData(String dirPath) {
        if (dirPath != null) {
            return nativeGetLogData(dirPath);
        }
        throw new IllegalArgumentException("dirPath may not be null.");
    }

    public CallSessionFileRotatingLogSink(String dirPath, int maxFileSize, Logging.Severity severity) {
        if (dirPath != null) {
            this.nativeSink = nativeAddSink(dirPath, maxFileSize, severity.ordinal());
            return;
        }
        throw new IllegalArgumentException("dirPath may not be null.");
    }

    public void dispose() {
        long j = this.nativeSink;
        if (j != 0) {
            nativeDeleteSink(j);
            this.nativeSink = 0;
        }
    }
}
