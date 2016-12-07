package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzrs<T> {
    private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    private static zza zB = null;
    private static int zC = 0;
    private static final Object zzaok = new Object();
    private T zD = null;
    protected final String zzbaf;
    protected final T zzbag;

    private interface zza {
        Long getLong(String str, Long l);

        String getString(String str, String str2);

        Boolean zza(String str, Boolean bool);

        Float zzb(String str, Float f);

        Integer zzb(String str, Integer num);
    }

    class AnonymousClass1 extends zzrs<Boolean> {
        AnonymousClass1(String str, Boolean bool) {
            super(str, bool);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhh(str);
        }

        protected Boolean zzhh(String str) {
            return null.zza(this.zzbaf, (Boolean) this.zzbag);
        }
    }

    class AnonymousClass2 extends zzrs<Long> {
        AnonymousClass2(String str, Long l) {
            super(str, l);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhi(str);
        }

        protected Long zzhi(String str) {
            return null.getLong(this.zzbaf, (Long) this.zzbag);
        }
    }

    class AnonymousClass3 extends zzrs<Integer> {
        AnonymousClass3(String str, Integer num) {
            super(str, num);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhj(str);
        }

        protected Integer zzhj(String str) {
            return null.zzb(this.zzbaf, (Integer) this.zzbag);
        }
    }

    class AnonymousClass4 extends zzrs<Float> {
        AnonymousClass4(String str, Float f) {
            super(str, f);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhk(str);
        }

        protected Float zzhk(String str) {
            return null.zzb(this.zzbaf, (Float) this.zzbag);
        }
    }

    class AnonymousClass5 extends zzrs<String> {
        AnonymousClass5(String str, String str2) {
            super(str, str2);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhl(str);
        }

        protected String zzhl(String str) {
            return null.getString(this.zzbaf, (String) this.zzbag);
        }
    }

    protected zzrs(String str, T t) {
        this.zzbaf = str;
        this.zzbag = t;
    }

    public static zzrs<Float> zza(String str, Float f) {
        return new AnonymousClass4(str, f);
    }

    public static zzrs<Integer> zza(String str, Integer num) {
        return new AnonymousClass3(str, num);
    }

    public static zzrs<Long> zza(String str, Long l) {
        return new AnonymousClass2(str, l);
    }

    public static zzrs<String> zzab(String str, String str2) {
        return new AnonymousClass5(str, str2);
    }

    public static zzrs<Boolean> zzm(String str, boolean z) {
        return new AnonymousClass1(str, Boolean.valueOf(z));
    }

    public final T get() {
        T zzhg;
        long clearCallingIdentity;
        try {
            zzhg = zzhg(this.zzbaf);
        } catch (SecurityException e) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            zzhg = zzhg(this.zzbaf);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        return zzhg;
    }

    protected abstract T zzhg(String str);
}
