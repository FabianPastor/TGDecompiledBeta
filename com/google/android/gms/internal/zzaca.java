package com.google.android.gms.internal;

public abstract class zzaca<T> {
    private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    private static zza zzaDC = null;
    private static int zzaDD = 0;
    private static final Object zztX = new Object();
    protected final String zzAX;
    protected final T zzAY;
    private T zzaDE = null;

    private interface zza {
    }

    class AnonymousClass1 extends zzaca<Boolean> {
        AnonymousClass1(String str, Boolean bool) {
            super(str, bool);
        }
    }

    class AnonymousClass2 extends zzaca<Long> {
        AnonymousClass2(String str, Long l) {
            super(str, l);
        }
    }

    class AnonymousClass3 extends zzaca<Integer> {
        AnonymousClass3(String str, Integer num) {
            super(str, num);
        }
    }

    class AnonymousClass4 extends zzaca<Float> {
        AnonymousClass4(String str, Float f) {
            super(str, f);
        }
    }

    class AnonymousClass5 extends zzaca<String> {
        AnonymousClass5(String str, String str2) {
            super(str, str2);
        }
    }

    protected zzaca(String str, T t) {
        this.zzAX = str;
        this.zzAY = t;
    }

    public static zzaca<String> zzB(String str, String str2) {
        return new AnonymousClass5(str, str2);
    }

    public static zzaca<Float> zza(String str, Float f) {
        return new AnonymousClass4(str, f);
    }

    public static zzaca<Integer> zza(String str, Integer num) {
        return new AnonymousClass3(str, num);
    }

    public static zzaca<Long> zza(String str, Long l) {
        return new AnonymousClass2(str, l);
    }

    public static zzaca<Boolean> zzj(String str, boolean z) {
        return new AnonymousClass1(str, Boolean.valueOf(z));
    }
}
