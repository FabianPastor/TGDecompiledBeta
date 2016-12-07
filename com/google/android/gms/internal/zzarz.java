package com.google.android.gms.internal;

import java.io.IOException;

public class zzarz extends IOException {
    public zzarz(String str) {
        super(str);
    }

    static zzarz cr() {
        return new zzarz("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
    }

    static zzarz cs() {
        return new zzarz("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    static zzarz ct() {
        return new zzarz("CodedInputStream encountered a malformed varint.");
    }

    static zzarz cu() {
        return new zzarz("Protocol message contained an invalid tag (zero).");
    }

    static zzarz cv() {
        return new zzarz("Protocol message end-group tag did not match expected tag.");
    }

    static zzarz cw() {
        return new zzarz("Protocol message tag had invalid wire type.");
    }

    static zzarz cx() {
        return new zzarz("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
}
