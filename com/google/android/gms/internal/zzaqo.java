package com.google.android.gms.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class zzaqo<T> {
    final Type bqi;
    final Class<? super T> brm;
    final int brn;

    protected zzaqo() {
        this.bqi = zzq(getClass());
        this.brm = zzapr.zzf(this.bqi);
        this.brn = this.bqi.hashCode();
    }

    zzaqo(Type type) {
        this.bqi = zzapr.zze((Type) zzapq.zzy(type));
        this.brm = zzapr.zzf(this.bqi);
        this.brn = this.bqi.hashCode();
    }

    public static zzaqo<?> zzl(Type type) {
        return new zzaqo(type);
    }

    static Type zzq(Class<?> cls) {
        Type genericSuperclass = cls.getGenericSuperclass();
        if (!(genericSuperclass instanceof Class)) {
            return zzapr.zze(((ParameterizedType) genericSuperclass).getActualTypeArguments()[0]);
        }
        throw new RuntimeException("Missing type parameter.");
    }

    public static <T> zzaqo<T> zzr(Class<T> cls) {
        return new zzaqo(cls);
    }

    public final Class<? super T> bB() {
        return this.brm;
    }

    public final Type bC() {
        return this.bqi;
    }

    public final boolean equals(Object obj) {
        return (obj instanceof zzaqo) && zzapr.zza(this.bqi, ((zzaqo) obj).bqi);
    }

    public final int hashCode() {
        return this.brn;
    }

    public final String toString() {
        return zzapr.zzg(this.bqi);
    }
}
