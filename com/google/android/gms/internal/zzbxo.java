package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzbxo<M extends zzbxn<M>, T> {
    public final int tag;
    protected final int type;
    protected final Class<T> zzckM;
    protected final boolean zzcuJ;

    private zzbxo(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.zzckM = cls;
        this.tag = i2;
        this.zzcuJ = z;
    }

    public static <M extends zzbxn<M>, T extends zzbxt> zzbxo<M, T> zza(int i, Class<T> cls, long j) {
        return new zzbxo(i, cls, (int) j, false);
    }

    private T zzad(List<zzbxv> list) {
        int i;
        int i2 = 0;
        List arrayList = new ArrayList();
        for (i = 0; i < list.size(); i++) {
            zzbxv com_google_android_gms_internal_zzbxv = (zzbxv) list.get(i);
            if (com_google_android_gms_internal_zzbxv.zzbxZ.length != 0) {
                zza(com_google_android_gms_internal_zzbxv, arrayList);
            }
        }
        i = arrayList.size();
        if (i == 0) {
            return null;
        }
        T cast = this.zzckM.cast(Array.newInstance(this.zzckM.getComponentType(), i));
        while (i2 < i) {
            Array.set(cast, i2, arrayList.get(i2));
            i2++;
        }
        return cast;
    }

    private T zzae(List<zzbxv> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.zzckM.cast(zzaN(zzbxl.zzaf(((zzbxv) list.get(list.size() - 1)).zzbxZ)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbxo)) {
            return false;
        }
        zzbxo com_google_android_gms_internal_zzbxo = (zzbxo) obj;
        return this.type == com_google_android_gms_internal_zzbxo.type && this.zzckM == com_google_android_gms_internal_zzbxo.zzckM && this.tag == com_google_android_gms_internal_zzbxo.tag && this.zzcuJ == com_google_android_gms_internal_zzbxo.zzcuJ;
    }

    public int hashCode() {
        return (this.zzcuJ ? 1 : 0) + ((((((this.type + 1147) * 31) + this.zzckM.hashCode()) * 31) + this.tag) * 31);
    }

    protected void zza(zzbxv com_google_android_gms_internal_zzbxv, List<Object> list) {
        list.add(zzaN(zzbxl.zzaf(com_google_android_gms_internal_zzbxv.zzbxZ)));
    }

    void zza(Object obj, zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
        if (this.zzcuJ) {
            zzc(obj, com_google_android_gms_internal_zzbxm);
        } else {
            zzb(obj, com_google_android_gms_internal_zzbxm);
        }
    }

    protected Object zzaN(zzbxl com_google_android_gms_internal_zzbxl) {
        String valueOf;
        Class componentType = this.zzcuJ ? this.zzckM.getComponentType() : this.zzckM;
        try {
            zzbxt com_google_android_gms_internal_zzbxt;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_zzbxt = (zzbxt) componentType.newInstance();
                    com_google_android_gms_internal_zzbxl.zza(com_google_android_gms_internal_zzbxt, zzbxw.zzrs(this.tag));
                    return com_google_android_gms_internal_zzbxt;
                case 11:
                    com_google_android_gms_internal_zzbxt = (zzbxt) componentType.newInstance();
                    com_google_android_gms_internal_zzbxl.zza(com_google_android_gms_internal_zzbxt);
                    return com_google_android_gms_internal_zzbxt;
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

    int zzaU(Object obj) {
        return this.zzcuJ ? zzaV(obj) : zzaW(obj);
    }

    protected int zzaV(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += zzaW(Array.get(obj, i2));
            }
        }
        return i;
    }

    protected int zzaW(Object obj) {
        int zzrs = zzbxw.zzrs(this.tag);
        switch (this.type) {
            case 10:
                return zzbxm.zzb(zzrs, (zzbxt) obj);
            case 11:
                return zzbxm.zzc(zzrs, (zzbxt) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    final T zzac(List<zzbxv> list) {
        return list == null ? null : this.zzcuJ ? zzad(list) : zzae(list);
    }

    protected void zzb(Object obj, zzbxm com_google_android_gms_internal_zzbxm) {
        try {
            com_google_android_gms_internal_zzbxm.zzrk(this.tag);
            switch (this.type) {
                case 10:
                    zzbxt com_google_android_gms_internal_zzbxt = (zzbxt) obj;
                    int zzrs = zzbxw.zzrs(this.tag);
                    com_google_android_gms_internal_zzbxm.zzb(com_google_android_gms_internal_zzbxt);
                    com_google_android_gms_internal_zzbxm.zzN(zzrs, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_zzbxm.zzc((zzbxt) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected void zzc(Object obj, zzbxm com_google_android_gms_internal_zzbxm) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                zzb(obj2, com_google_android_gms_internal_zzbxm);
            }
        }
    }
}
