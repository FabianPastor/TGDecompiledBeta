package com.google.android.gms.internal;

import java.io.IOException;

public final class ads {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static int zzcsn = 11;
    private static int zzcso = 12;
    private static int zzcsp = 16;
    private static int zzcsq = 26;
    public static final int[] zzcsr = new int[0];
    public static final long[] zzcss = new long[0];
    public static final float[] zzcst = new float[0];
    private static double[] zzcsu = new double[0];
    public static final boolean[] zzcsv = new boolean[0];
    public static final byte[][] zzcsw = new byte[0][];
    public static final byte[] zzcsx = new byte[0];

    public static final int zzb(adg com_google_android_gms_internal_adg, int i) throws IOException {
        int i2 = 1;
        int position = com_google_android_gms_internal_adg.getPosition();
        com_google_android_gms_internal_adg.zzcm(i);
        while (com_google_android_gms_internal_adg.zzLB() == i) {
            com_google_android_gms_internal_adg.zzcm(i);
            i2++;
        }
        com_google_android_gms_internal_adg.zzq(position, i);
        return i2;
    }
}
