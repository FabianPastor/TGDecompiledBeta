package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzvq<T> {
    private final int zzbcm;
    private final String zzbcn;
    private final T zzbco;

    public static class zza extends zzvq<Boolean> {
        public zza(int i, String str, Boolean bool) {
            super(i, str, bool);
        }

        public /* synthetic */ Object zza(zzvt com_google_android_gms_internal_zzvt) {
            return zzb(com_google_android_gms_internal_zzvt);
        }

        public Boolean zzb(zzvt com_google_android_gms_internal_zzvt) {
            try {
                return Boolean.valueOf(com_google_android_gms_internal_zzvt.getBooleanFlagValue(getKey(), ((Boolean) zzlp()).booleanValue(), getSource()));
            } catch (RemoteException e) {
                return (Boolean) zzlp();
            }
        }
    }

    public static class zzb extends zzvq<Integer> {
        public zzb(int i, String str, Integer num) {
            super(i, str, num);
        }

        public /* synthetic */ Object zza(zzvt com_google_android_gms_internal_zzvt) {
            return zzc(com_google_android_gms_internal_zzvt);
        }

        public Integer zzc(zzvt com_google_android_gms_internal_zzvt) {
            try {
                return Integer.valueOf(com_google_android_gms_internal_zzvt.getIntFlagValue(getKey(), ((Integer) zzlp()).intValue(), getSource()));
            } catch (RemoteException e) {
                return (Integer) zzlp();
            }
        }
    }

    public static class zzc extends zzvq<Long> {
        public zzc(int i, String str, Long l) {
            super(i, str, l);
        }

        public /* synthetic */ Object zza(zzvt com_google_android_gms_internal_zzvt) {
            return zzd(com_google_android_gms_internal_zzvt);
        }

        public Long zzd(zzvt com_google_android_gms_internal_zzvt) {
            try {
                return Long.valueOf(com_google_android_gms_internal_zzvt.getLongFlagValue(getKey(), ((Long) zzlp()).longValue(), getSource()));
            } catch (RemoteException e) {
                return (Long) zzlp();
            }
        }
    }

    public static class zzd extends zzvq<String> {
        public zzd(int i, String str, String str2) {
            super(i, str, str2);
        }

        public /* synthetic */ Object zza(zzvt com_google_android_gms_internal_zzvt) {
            return zze(com_google_android_gms_internal_zzvt);
        }

        public String zze(zzvt com_google_android_gms_internal_zzvt) {
            try {
                return com_google_android_gms_internal_zzvt.getStringFlagValue(getKey(), (String) zzlp(), getSource());
            } catch (RemoteException e) {
                return (String) zzlp();
            }
        }
    }

    private zzvq(int i, String str, T t) {
        this.zzbcm = i;
        this.zzbcn = str;
        this.zzbco = t;
        zzvu.zzbhe().zza(this);
    }

    public static zza zzb(int i, String str, Boolean bool) {
        return new zza(i, str, bool);
    }

    public static zzb zzb(int i, String str, int i2) {
        return new zzb(i, str, Integer.valueOf(i2));
    }

    public static zzc zzb(int i, String str, long j) {
        return new zzc(i, str, Long.valueOf(j));
    }

    public static zzd zzc(int i, String str, String str2) {
        return new zzd(i, str, str2);
    }

    public T get() {
        return zzvu.zzbhf().zzb(this);
    }

    public String getKey() {
        return this.zzbcn;
    }

    public int getSource() {
        return this.zzbcm;
    }

    protected abstract T zza(zzvt com_google_android_gms_internal_zzvt);

    public T zzlp() {
        return this.zzbco;
    }
}
