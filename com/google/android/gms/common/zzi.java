package com.google.android.gms.common;

import java.util.Arrays;

final class zzi extends zzh {
    private final byte[] zzflc;

    zzi(byte[] bArr) {
        super(Arrays.copyOfRange(bArr, 0, 25));
        this.zzflc = bArr;
    }

    final byte[] getBytes() {
        return this.zzflc;
    }
}
