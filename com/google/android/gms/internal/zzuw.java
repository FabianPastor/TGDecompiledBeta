package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzuw<T> {
    private final int zzbae;
    private final String zzbaf;
    private final T zzbag;

    public static class zza extends zzuw<Boolean> {
        public zza(int i, String str, Boolean bool) {
            super(i, str, bool);
        }

        public /* synthetic */ Object zza(zzuz com_google_android_gms_internal_zzuz) {
            return zzb(com_google_android_gms_internal_zzuz);
        }

        public Boolean zzb(zzuz com_google_android_gms_internal_zzuz) {
            try {
                return Boolean.valueOf(com_google_android_gms_internal_zzuz.getBooleanFlagValue(getKey(), ((Boolean) zzkq()).booleanValue(), getSource()));
            } catch (RemoteException e) {
                return (Boolean) zzkq();
            }
        }
    }

    public static class zzb extends zzuw<Integer> {
        public zzb(int i, String str, Integer num) {
            super(i, str, num);
        }

        public /* synthetic */ Object zza(zzuz com_google_android_gms_internal_zzuz) {
            return zzc(com_google_android_gms_internal_zzuz);
        }

        public Integer zzc(zzuz com_google_android_gms_internal_zzuz) {
            try {
                return Integer.valueOf(com_google_android_gms_internal_zzuz.getIntFlagValue(getKey(), ((Integer) zzkq()).intValue(), getSource()));
            } catch (RemoteException e) {
                return (Integer) zzkq();
            }
        }
    }

    public static class zzc extends zzuw<Long> {
        public zzc(int i, String str, Long l) {
            super(i, str, l);
        }

        public /* synthetic */ Object zza(zzuz com_google_android_gms_internal_zzuz) {
            return zzd(com_google_android_gms_internal_zzuz);
        }

        public Long zzd(zzuz com_google_android_gms_internal_zzuz) {
            try {
                return Long.valueOf(com_google_android_gms_internal_zzuz.getLongFlagValue(getKey(), ((Long) zzkq()).longValue(), getSource()));
            } catch (RemoteException e) {
                return (Long) zzkq();
            }
        }
    }

    public static class zzd extends zzuw<String> {
        public zzd(int i, String str, String str2) {
            super(i, str, str2);
        }

        public /* synthetic */ Object zza(zzuz com_google_android_gms_internal_zzuz) {
            return zze(com_google_android_gms_internal_zzuz);
        }

        public String zze(zzuz com_google_android_gms_internal_zzuz) {
            try {
                return com_google_android_gms_internal_zzuz.getStringFlagValue(getKey(), (String) zzkq(), getSource());
            } catch (RemoteException e) {
                return (String) zzkq();
            }
        }
    }

    private zzuw(int i, String str, T t) {
        this.zzbae = i;
        this.zzbaf = str;
        this.zzbag = t;
        zzva.zzbhm().zza(this);
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
        return zzva.zzbhn().zzb(this);
    }

    public String getKey() {
        return this.zzbaf;
    }

    public int getSource() {
        return this.zzbae;
    }

    protected abstract T zza(zzuz com_google_android_gms_internal_zzuz);

    public T zzkq() {
        return this.zzbag;
    }
}
