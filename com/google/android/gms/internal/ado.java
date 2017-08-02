package com.google.android.gms.internal;

import java.io.IOException;

public final class ado extends IOException {
    public ado(String str) {
        super(str);
    }

    static ado zzLQ() {
        return new ado("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
    }

    static ado zzLR() {
        return new ado("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    static ado zzLS() {
        return new ado("CodedInputStream encountered a malformed varint.");
    }

    static ado zzLT() {
        return new ado("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
}
