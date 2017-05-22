package com.google.android.gms.internal;

import java.io.IOException;

public class zzbyi extends IOException {
    public zzbyi(String str) {
        super(str);
    }

    static zzbyi zzaft() {
        return new zzbyi("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
    }

    static zzbyi zzafu() {
        return new zzbyi("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    static zzbyi zzafv() {
        return new zzbyi("CodedInputStream encountered a malformed varint.");
    }

    static zzbyi zzafw() {
        return new zzbyi("Protocol message contained an invalid tag (zero).");
    }

    static zzbyi zzafx() {
        return new zzbyi("Protocol message end-group tag did not match expected tag.");
    }

    static zzbyi zzafy() {
        return new zzbyi("Protocol message tag had invalid wire type.");
    }

    static zzbyi zzafz() {
        return new zzbyi("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
}
