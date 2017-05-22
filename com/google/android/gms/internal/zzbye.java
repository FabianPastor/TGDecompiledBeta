package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzbye<M extends zzbyd<M>, T> {
    public final int tag;
    protected final int type;
    protected final Class<T> zzckL;
    protected final boolean zzcwD;

    private zzbye(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.zzckL = cls;
        this.tag = i2;
        this.zzcwD = z;
    }

    public static <M extends zzbyd<M>, T extends zzbyj> zzbye<M, T> zza(int i, Class<T> cls, long j) {
        return new zzbye(i, cls, (int) j, false);
    }

    private T zzae(List<zzbyl> list) {
        int i;
        int i2 = 0;
        List arrayList = new ArrayList();
        for (i = 0; i < list.size(); i++) {
            zzbyl com_google_android_gms_internal_zzbyl = (zzbyl) list.get(i);
            if (com_google_android_gms_internal_zzbyl.zzbyc.length != 0) {
                zza(com_google_android_gms_internal_zzbyl, arrayList);
            }
        }
        i = arrayList.size();
        if (i == 0) {
            return null;
        }
        T cast = this.zzckL.cast(Array.newInstance(this.zzckL.getComponentType(), i));
        while (i2 < i) {
            Array.set(cast, i2, arrayList.get(i2));
            i2++;
        }
        return cast;
    }

    private T zzaf(List<zzbyl> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.zzckL.cast(zzaU(zzbyb.zzag(((zzbyl) list.get(list.size() - 1)).zzbyc)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbye)) {
            return false;
        }
        zzbye com_google_android_gms_internal_zzbye = (zzbye) obj;
        return this.type == com_google_android_gms_internal_zzbye.type && this.zzckL == com_google_android_gms_internal_zzbye.zzckL && this.tag == com_google_android_gms_internal_zzbye.tag && this.zzcwD == com_google_android_gms_internal_zzbye.zzcwD;
    }

    public int hashCode() {
        return (this.zzcwD ? 1 : 0) + ((((((this.type + 1147) * 31) + this.zzckL.hashCode()) * 31) + this.tag) * 31);
    }

    protected void zza(zzbyl com_google_android_gms_internal_zzbyl, List<Object> list) {
        list.add(zzaU(zzbyb.zzag(com_google_android_gms_internal_zzbyl.zzbyc)));
    }

    void zza(Object obj, zzbyc com_google_android_gms_internal_zzbyc) throws IOException {
        if (this.zzcwD) {
            zzc(obj, com_google_android_gms_internal_zzbyc);
        } else {
            zzb(obj, com_google_android_gms_internal_zzbyc);
        }
    }

    protected Object zzaU(zzbyb com_google_android_gms_internal_zzbyb) {
        String valueOf;
        Class componentType = this.zzcwD ? this.zzckL.getComponentType() : this.zzckL;
        try {
            zzbyj com_google_android_gms_internal_zzbyj;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_zzbyj = (zzbyj) componentType.newInstance();
                    com_google_android_gms_internal_zzbyb.zza(com_google_android_gms_internal_zzbyj, zzbym.zzrx(this.tag));
                    return com_google_android_gms_internal_zzbyj;
                case 11:
                    com_google_android_gms_internal_zzbyj = (zzbyj) componentType.newInstance();
                    com_google_android_gms_internal_zzbyb.zza(com_google_android_gms_internal_zzbyj);
                    return com_google_android_gms_internal_zzbyj;
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

    int zzaV(Object obj) {
        return this.zzcwD ? zzaW(obj) : zzaX(obj);
    }

    protected int zzaW(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += zzaX(Array.get(obj, i2));
            }
        }
        return i;
    }

    protected int zzaX(Object obj) {
        int zzrx = zzbym.zzrx(this.tag);
        switch (this.type) {
            case 10:
                return zzbyc.zzb(zzrx, (zzbyj) obj);
            case 11:
                return zzbyc.zzc(zzrx, (zzbyj) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    final T zzad(List<zzbyl> list) {
        return list == null ? null : this.zzcwD ? zzae(list) : zzaf(list);
    }

    protected void zzb(Object obj, zzbyc com_google_android_gms_internal_zzbyc) {
        try {
            com_google_android_gms_internal_zzbyc.zzrp(this.tag);
            switch (this.type) {
                case 10:
                    zzbyj com_google_android_gms_internal_zzbyj = (zzbyj) obj;
                    int zzrx = zzbym.zzrx(this.tag);
                    com_google_android_gms_internal_zzbyc.zzb(com_google_android_gms_internal_zzbyj);
                    com_google_android_gms_internal_zzbyc.zzN(zzrx, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_zzbyc.zzc((zzbyj) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected void zzc(Object obj, zzbyc com_google_android_gms_internal_zzbyc) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                zzb(obj2, com_google_android_gms_internal_zzbyc);
            }
        }
    }
}
