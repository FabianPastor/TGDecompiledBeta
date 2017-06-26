package com.google.android.gms.internal;

public class zzbey<T> {
    private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    private static zzbfe zzaFo = null;
    private static int zzaFp = 0;
    private static final Object zzuH = new Object();
    private String zzBP;
    private T zzBQ;
    private T zzaFq = null;

    protected zzbey(String str, T t) {
        this.zzBP = str;
        this.zzBQ = t;
    }

    public static zzbey<Float> zza(String str, Float f) {
        return new zzbfc(str, f);
    }

    public static zzbey<Integer> zza(String str, Integer num) {
        return new zzbfb(str, num);
    }

    public static zzbey<Long> zza(String str, Long l) {
        return new zzbfa(str, l);
    }

    public static zzbey<Boolean> zzg(String str, boolean z) {
        return new zzbez(str, Boolean.valueOf(z));
    }

    public static zzbey<String> zzv(String str, String str2) {
        return new zzbfd(str, str2);
    }
}
