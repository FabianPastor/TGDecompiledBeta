package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzaqa<T> {
    private final int zzAW;
    private final String zzAX;
    private final T zzAY;

    public static class zza extends zzaqa<Boolean> {
        public zza(int i, String str, Boolean bool) {
            super(i, str, bool);
        }

        public /* synthetic */ Object zza(zzaqd com_google_android_gms_internal_zzaqd) {
            return zzb(com_google_android_gms_internal_zzaqd);
        }

        public Boolean zzb(zzaqd com_google_android_gms_internal_zzaqd) {
            try {
                return Boolean.valueOf(com_google_android_gms_internal_zzaqd.getBooleanFlagValue(getKey(), ((Boolean) zzfr()).booleanValue(), getSource()));
            } catch (RemoteException e) {
                return (Boolean) zzfr();
            }
        }
    }

    public static class zzb extends zzaqa<Integer> {
        public zzb(int i, String str, Integer num) {
            super(i, str, num);
        }

        public /* synthetic */ Object zza(zzaqd com_google_android_gms_internal_zzaqd) {
            return zzc(com_google_android_gms_internal_zzaqd);
        }

        public Integer zzc(zzaqd com_google_android_gms_internal_zzaqd) {
            try {
                return Integer.valueOf(com_google_android_gms_internal_zzaqd.getIntFlagValue(getKey(), ((Integer) zzfr()).intValue(), getSource()));
            } catch (RemoteException e) {
                return (Integer) zzfr();
            }
        }
    }

    public static class zzc extends zzaqa<Long> {
        public zzc(int i, String str, Long l) {
            super(i, str, l);
        }

        public /* synthetic */ Object zza(zzaqd com_google_android_gms_internal_zzaqd) {
            return zzd(com_google_android_gms_internal_zzaqd);
        }

        public Long zzd(zzaqd com_google_android_gms_internal_zzaqd) {
            try {
                return Long.valueOf(com_google_android_gms_internal_zzaqd.getLongFlagValue(getKey(), ((Long) zzfr()).longValue(), getSource()));
            } catch (RemoteException e) {
                return (Long) zzfr();
            }
        }
    }

    public static class zzd extends zzaqa<String> {
        public zzd(int i, String str, String str2) {
            super(i, str, str2);
        }

        public /* synthetic */ Object zza(zzaqd com_google_android_gms_internal_zzaqd) {
            return zze(com_google_android_gms_internal_zzaqd);
        }

        public String zze(zzaqd com_google_android_gms_internal_zzaqd) {
            try {
                return com_google_android_gms_internal_zzaqd.getStringFlagValue(getKey(), (String) zzfr(), getSource());
            } catch (RemoteException e) {
                return (String) zzfr();
            }
        }
    }

    private zzaqa(int i, String str, T t) {
        this.zzAW = i;
        this.zzAX = str;
        this.zzAY = t;
        zzaqe.zzDD().zza(this);
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
        return zzaqe.zzDE().zzb(this);
    }

    public String getKey() {
        return this.zzAX;
    }

    public int getSource() {
        return this.zzAW;
    }

    protected abstract T zza(zzaqd com_google_android_gms_internal_zzaqd);

    public T zzfr() {
        return this.zzAY;
    }
}
