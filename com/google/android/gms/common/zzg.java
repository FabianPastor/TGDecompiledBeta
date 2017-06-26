package com.google.android.gms.common;

import android.util.Log;
import com.google.android.gms.common.internal.zzar;
import com.google.android.gms.common.internal.zzas;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzl;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

abstract class zzg extends zzas {
    private int zzaAg;

    protected zzg(byte[] bArr) {
        boolean z = false;
        if (bArr.length != 25) {
            int length = bArr.length;
            String valueOf = String.valueOf(zzl.zza(bArr, 0, bArr.length, false));
            Log.wtf("GoogleCertificates", new StringBuilder(String.valueOf(valueOf).length() + 51).append("Cert hash data has incorrect length (").append(length).append("):\n").append(valueOf).toString(), new Exception());
            bArr = Arrays.copyOfRange(bArr, 0, 25);
            if (bArr.length == 25) {
                z = true;
            }
            zzbo.zzb(z, "cert hash data has incorrect length. length=" + bArr.length);
        }
        this.zzaAg = Arrays.hashCode(bArr);
    }

    protected static byte[] zzcs(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof zzar)) {
            return false;
        }
        try {
            zzar com_google_android_gms_common_internal_zzar = (zzar) obj;
            if (com_google_android_gms_common_internal_zzar.zzoZ() != hashCode()) {
                return false;
            }
            IObjectWrapper zzoY = com_google_android_gms_common_internal_zzar.zzoY();
            if (zzoY == null) {
                return false;
            }
            return Arrays.equals(getBytes(), (byte[]) zzn.zzE(zzoY));
        } catch (Throwable e) {
            Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e);
            return false;
        }
    }

    abstract byte[] getBytes();

    public int hashCode() {
        return this.zzaAg;
    }

    public final IObjectWrapper zzoY() {
        return zzn.zzw(getBytes());
    }

    public final int zzoZ() {
        return hashCode();
    }
}
