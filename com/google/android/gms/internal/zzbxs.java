package com.google.android.gms.internal;

import java.io.IOException;

public class zzbxs extends IOException {
    public zzbxs(String str) {
        super(str);
    }

    static zzbxs zzaeL() {
        return new zzbxs("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
    }

    static zzbxs zzaeM() {
        return new zzbxs("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    static zzbxs zzaeN() {
        return new zzbxs("CodedInputStream encountered a malformed varint.");
    }

    static zzbxs zzaeO() {
        return new zzbxs("Protocol message contained an invalid tag (zero).");
    }

    static zzbxs zzaeP() {
        return new zzbxs("Protocol message end-group tag did not match expected tag.");
    }

    static zzbxs zzaeQ() {
        return new zzbxs("Protocol message tag had invalid wire type.");
    }

    static zzbxs zzaeR() {
        return new zzbxs("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
}
