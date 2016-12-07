package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbuw {
    public static final int[] zzcsi = new int[0];
    public static final long[] zzcsj = new long[0];
    public static final float[] zzcsk = new float[0];
    public static final double[] zzcsl = new double[0];
    public static final boolean[] zzcsm = new boolean[0];
    public static final String[] zzcsn = new String[0];
    public static final byte[][] zzcso = new byte[0][];
    public static final byte[] zzcsp = new byte[0];

    public static int zzK(int i, int i2) {
        return (i << 3) | i2;
    }

    public static boolean zzb(zzbul com_google_android_gms_internal_zzbul, int i) throws IOException {
        return com_google_android_gms_internal_zzbul.zzqh(i);
    }

    public static final int zzc(zzbul com_google_android_gms_internal_zzbul, int i) throws IOException {
        int i2 = 1;
        int position = com_google_android_gms_internal_zzbul.getPosition();
        com_google_android_gms_internal_zzbul.zzqh(i);
        while (com_google_android_gms_internal_zzbul.zzacu() == i) {
            com_google_android_gms_internal_zzbul.zzqh(i);
            i2++;
        }
        com_google_android_gms_internal_zzbul.zzql(position);
        return i2;
    }

    static int zzqA(int i) {
        return i & 7;
    }

    public static int zzqB(int i) {
        return i >>> 3;
    }
}
