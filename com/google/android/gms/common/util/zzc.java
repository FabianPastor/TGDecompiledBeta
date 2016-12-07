package com.google.android.gms.common.util;

import android.util.Base64;

public final class zzc {
    public static String zzq(byte[] bArr) {
        return bArr == null ? null : Base64.encodeToString(bArr, 0);
    }

    public static String zzr(byte[] bArr) {
        return bArr == null ? null : Base64.encodeToString(bArr, 10);
    }

    public static String zzs(byte[] bArr) {
        return bArr == null ? null : Base64.encodeToString(bArr, 11);
    }
}
