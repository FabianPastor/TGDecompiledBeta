package com.google.android.gms.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class zzapx<T> {
    final Type bmR;
    final Class<? super T> bnV;
    final int bnW;

    protected zzapx() {
        this.bmR = zzq(getClass());
        this.bnV = zzapa.zzf(this.bmR);
        this.bnW = this.bmR.hashCode();
    }

    zzapx(Type type) {
        this.bmR = zzapa.zze((Type) zzaoz.zzy(type));
        this.bnV = zzapa.zzf(this.bmR);
        this.bnW = this.bmR.hashCode();
    }

    public static zzapx<?> zzl(Type type) {
        return new zzapx(type);
    }

    static Type zzq(Class<?> cls) {
        Type genericSuperclass = cls.getGenericSuperclass();
        if (!(genericSuperclass instanceof Class)) {
            return zzapa.zze(((ParameterizedType) genericSuperclass).getActualTypeArguments()[0]);
        }
        throw new RuntimeException("Missing type parameter.");
    }

    public static <T> zzapx<T> zzr(Class<T> cls) {
        return new zzapx(cls);
    }

    public final Class<? super T> by() {
        return this.bnV;
    }

    public final Type bz() {
        return this.bmR;
    }

    public final boolean equals(Object obj) {
        return (obj instanceof zzapx) && zzapa.zza(this.bmR, ((zzapx) obj).bmR);
    }

    public final int hashCode() {
        return this.bnW;
    }

    public final String toString() {
        return zzapa.zzg(this.bmR);
    }
}
