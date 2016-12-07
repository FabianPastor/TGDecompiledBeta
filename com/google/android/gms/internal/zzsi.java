package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzsi<T> {
    private static zza BL = null;
    private static int BM = 0;
    private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    private static final Object zzaox = new Object();
    private T BN = null;
    protected final String zzbcn;
    protected final T zzbco;

    private interface zza {
        Long getLong(String str, Long l);

        String getString(String str, String str2);

        Boolean zza(String str, Boolean bool);

        Float zzb(String str, Float f);

        Integer zzb(String str, Integer num);
    }

    class AnonymousClass1 extends zzsi<Boolean> {
        AnonymousClass1(String str, Boolean bool) {
            super(str, bool);
        }

        protected /* synthetic */ Object zzhi(String str) {
            return zzhj(str);
        }

        protected Boolean zzhj(String str) {
            return null.zza(this.zzbcn, (Boolean) this.zzbco);
        }
    }

    class AnonymousClass2 extends zzsi<Long> {
        AnonymousClass2(String str, Long l) {
            super(str, l);
        }

        protected /* synthetic */ Object zzhi(String str) {
            return zzhk(str);
        }

        protected Long zzhk(String str) {
            return null.getLong(this.zzbcn, (Long) this.zzbco);
        }
    }

    class AnonymousClass3 extends zzsi<Integer> {
        AnonymousClass3(String str, Integer num) {
            super(str, num);
        }

        protected /* synthetic */ Object zzhi(String str) {
            return zzhl(str);
        }

        protected Integer zzhl(String str) {
            return null.zzb(this.zzbcn, (Integer) this.zzbco);
        }
    }

    class AnonymousClass4 extends zzsi<Float> {
        AnonymousClass4(String str, Float f) {
            super(str, f);
        }

        protected /* synthetic */ Object zzhi(String str) {
            return zzhm(str);
        }

        protected Float zzhm(String str) {
            return null.zzb(this.zzbcn, (Float) this.zzbco);
        }
    }

    class AnonymousClass5 extends zzsi<String> {
        AnonymousClass5(String str, String str2) {
            super(str, str2);
        }

        protected /* synthetic */ Object zzhi(String str) {
            return zzhn(str);
        }

        protected String zzhn(String str) {
            return null.getString(this.zzbcn, (String) this.zzbco);
        }
    }

    protected zzsi(String str, T t) {
        this.zzbcn = str;
        this.zzbco = t;
    }

    public static zzsi<Float> zza(String str, Float f) {
        return new AnonymousClass4(str, f);
    }

    public static zzsi<Integer> zza(String str, Integer num) {
        return new AnonymousClass3(str, num);
    }

    public static zzsi<Long> zza(String str, Long l) {
        return new AnonymousClass2(str, l);
    }

    public static zzsi<String> zzaa(String str, String str2) {
        return new AnonymousClass5(str, str2);
    }

    public static zzsi<Boolean> zzk(String str, boolean z) {
        return new AnonymousClass1(str, Boolean.valueOf(z));
    }

    public final T get() {
        T zzhi;
        long clearCallingIdentity;
        try {
            zzhi = zzhi(this.zzbcn);
        } catch (SecurityException e) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            zzhi = zzhi(this.zzbcn);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        return zzhi;
    }

    protected abstract T zzhi(String str);
}
