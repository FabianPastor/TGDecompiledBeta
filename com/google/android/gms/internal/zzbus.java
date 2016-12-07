package com.google.android.gms.internal;

import java.io.IOException;

public class zzbus extends IOException {
    public zzbus(String str) {
        super(str);
    }

    static zzbus zzacR() {
        return new zzbus("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
    }

    static zzbus zzacS() {
        return new zzbus("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    static zzbus zzacT() {
        return new zzbus("CodedInputStream encountered a malformed varint.");
    }

    static zzbus zzacU() {
        return new zzbus("Protocol message contained an invalid tag (zero).");
    }

    static zzbus zzacV() {
        return new zzbus("Protocol message end-group tag did not match expected tag.");
    }

    static zzbus zzacW() {
        return new zzbus("Protocol message tag had invalid wire type.");
    }

    static zzbus zzacX() {
        return new zzbus("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
}
