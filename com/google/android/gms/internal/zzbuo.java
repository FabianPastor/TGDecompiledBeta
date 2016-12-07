package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzbuo<M extends zzbun<M>, T> {
    public final int tag;
    protected final int type;
    protected final Class<T> zzciF;
    protected final boolean zzcrY;

    private zzbuo(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.zzciF = cls;
        this.tag = i2;
        this.zzcrY = z;
    }

    public static <M extends zzbun<M>, T extends zzbut> zzbuo<M, T> zza(int i, Class<T> cls, long j) {
        return new zzbuo(i, cls, (int) j, false);
    }

    private T zzaa(List<zzbuv> list) {
        int i;
        int i2 = 0;
        List arrayList = new ArrayList();
        for (i = 0; i < list.size(); i++) {
            zzbuv com_google_android_gms_internal_zzbuv = (zzbuv) list.get(i);
            if (com_google_android_gms_internal_zzbuv.zzcsh.length != 0) {
                zza(com_google_android_gms_internal_zzbuv, arrayList);
            }
        }
        i = arrayList.size();
        if (i == 0) {
            return null;
        }
        T cast = this.zzciF.cast(Array.newInstance(this.zzciF.getComponentType(), i));
        while (i2 < i) {
            Array.set(cast, i2, arrayList.get(i2));
            i2++;
        }
        return cast;
    }

    private T zzab(List<zzbuv> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.zzciF.cast(zzaM(zzbul.zzad(((zzbuv) list.get(list.size() - 1)).zzcsh)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbuo)) {
            return false;
        }
        zzbuo com_google_android_gms_internal_zzbuo = (zzbuo) obj;
        return this.type == com_google_android_gms_internal_zzbuo.type && this.zzciF == com_google_android_gms_internal_zzbuo.zzciF && this.tag == com_google_android_gms_internal_zzbuo.tag && this.zzcrY == com_google_android_gms_internal_zzbuo.zzcrY;
    }

    public int hashCode() {
        return (this.zzcrY ? 1 : 0) + ((((((this.type + 1147) * 31) + this.zzciF.hashCode()) * 31) + this.tag) * 31);
    }

    final T zzZ(List<zzbuv> list) {
        return list == null ? null : this.zzcrY ? zzaa(list) : zzab(list);
    }

    protected void zza(zzbuv com_google_android_gms_internal_zzbuv, List<Object> list) {
        list.add(zzaM(zzbul.zzad(com_google_android_gms_internal_zzbuv.zzcsh)));
    }

    void zza(Object obj, zzbum com_google_android_gms_internal_zzbum) throws IOException {
        if (this.zzcrY) {
            zzc(obj, com_google_android_gms_internal_zzbum);
        } else {
            zzb(obj, com_google_android_gms_internal_zzbum);
        }
    }

    protected Object zzaM(zzbul com_google_android_gms_internal_zzbul) {
        String valueOf;
        Class componentType = this.zzcrY ? this.zzciF.getComponentType() : this.zzciF;
        try {
            zzbut com_google_android_gms_internal_zzbut;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_zzbut = (zzbut) componentType.newInstance();
                    com_google_android_gms_internal_zzbul.zza(com_google_android_gms_internal_zzbut, zzbuw.zzqB(this.tag));
                    return com_google_android_gms_internal_zzbut;
                case 11:
                    com_google_android_gms_internal_zzbut = (zzbut) componentType.newInstance();
                    com_google_android_gms_internal_zzbul.zza(com_google_android_gms_internal_zzbut);
                    return com_google_android_gms_internal_zzbut;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            valueOf = String.valueOf(componentType);
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("Error creating instance of class ").append(valueOf).toString(), e);
        } catch (Throwable e2) {
            valueOf = String.valueOf(componentType);
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("Error creating instance of class ").append(valueOf).toString(), e2);
        } catch (Throwable e22) {
            throw new IllegalArgumentException("Error reading extension field", e22);
        }
    }

    int zzaR(Object obj) {
        return this.zzcrY ? zzaS(obj) : zzaT(obj);
    }

    protected int zzaS(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += zzaT(Array.get(obj, i2));
            }
        }
        return i;
    }

    protected int zzaT(Object obj) {
        int zzqB = zzbuw.zzqB(this.tag);
        switch (this.type) {
            case 10:
                return zzbum.zzb(zzqB, (zzbut) obj);
            case 11:
                return zzbum.zzc(zzqB, (zzbut) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    protected void zzb(Object obj, zzbum com_google_android_gms_internal_zzbum) {
        try {
            com_google_android_gms_internal_zzbum.zzqt(this.tag);
            switch (this.type) {
                case 10:
                    zzbut com_google_android_gms_internal_zzbut = (zzbut) obj;
                    int zzqB = zzbuw.zzqB(this.tag);
                    com_google_android_gms_internal_zzbum.zzb(com_google_android_gms_internal_zzbut);
                    com_google_android_gms_internal_zzbum.zzJ(zzqB, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_zzbum.zzc((zzbut) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected void zzc(Object obj, zzbum com_google_android_gms_internal_zzbum) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                zzb(obj2, com_google_android_gms_internal_zzbum);
            }
        }
    }
}
