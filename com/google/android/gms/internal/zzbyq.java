package com.google.android.gms.internal;

import java.io.IOException;
import java.io.InterruptedIOException;

public class zzbyq extends zzbzc {
    protected IOException newTimeoutException(IOException iOException) {
        IOException interruptedIOException = new InterruptedIOException("timeout");
        if (iOException != null) {
            interruptedIOException.initCause(iOException);
        }
        return interruptedIOException;
    }

    protected void timedOut() {
    }
}
