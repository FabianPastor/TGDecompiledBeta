package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzarf<M extends zzare<M>, T> {
    protected final Class<T> bhd;
    protected final boolean bqw;
    public final int tag;
    protected final int type;

    private zzarf(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.bhd = cls;
        this.tag = i2;
        this.bqw = z;
    }

    public static <M extends zzare<M>, T extends zzark> zzarf<M, T> zza(int i, Class<T> cls, long j) {
        return new zzarf(i, cls, (int) j, false);
    }

    private T zzaz(List<zzarm> list) {
        int i;
        int i2 = 0;
        List arrayList = new ArrayList();
        for (i = 0; i < list.size(); i++) {
            zzarm com_google_android_gms_internal_zzarm = (zzarm) list.get(i);
            if (com_google_android_gms_internal_zzarm.avk.length != 0) {
                zza(com_google_android_gms_internal_zzarm, arrayList);
            }
        }
        i = arrayList.size();
        if (i == 0) {
            return null;
        }
        T cast = this.bhd.cast(Array.newInstance(this.bhd.getComponentType(), i));
        while (i2 < i) {
            Array.set(cast, i2, arrayList.get(i2));
            i2++;
        }
        return cast;
    }

    private T zzba(List<zzarm> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.bhd.cast(zzck(zzarc.zzbd(((zzarm) list.get(list.size() - 1)).avk)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzarf)) {
            return false;
        }
        zzarf com_google_android_gms_internal_zzarf = (zzarf) obj;
        return this.type == com_google_android_gms_internal_zzarf.type && this.bhd == com_google_android_gms_internal_zzarf.bhd && this.tag == com_google_android_gms_internal_zzarf.tag && this.bqw == com_google_android_gms_internal_zzarf.bqw;
    }

    public int hashCode() {
        return (this.bqw ? 1 : 0) + ((((((this.type + 1147) * 31) + this.bhd.hashCode()) * 31) + this.tag) * 31);
    }

    protected void zza(zzarm com_google_android_gms_internal_zzarm, List<Object> list) {
        list.add(zzck(zzarc.zzbd(com_google_android_gms_internal_zzarm.avk)));
    }

    void zza(Object obj, zzard com_google_android_gms_internal_zzard) throws IOException {
        if (this.bqw) {
            zzc(obj, com_google_android_gms_internal_zzard);
        } else {
            zzb(obj, com_google_android_gms_internal_zzard);
        }
    }

    final T zzay(List<zzarm> list) {
        return list == null ? null : this.bqw ? zzaz(list) : zzba(list);
    }

    protected void zzb(Object obj, zzard com_google_android_gms_internal_zzard) {
        try {
            com_google_android_gms_internal_zzard.zzahm(this.tag);
            switch (this.type) {
                case 10:
                    zzark com_google_android_gms_internal_zzark = (zzark) obj;
                    int zzahu = zzarn.zzahu(this.tag);
                    com_google_android_gms_internal_zzard.zzb(com_google_android_gms_internal_zzark);
                    com_google_android_gms_internal_zzard.zzai(zzahu, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_zzard.zzc((zzark) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected void zzc(Object obj, zzard com_google_android_gms_internal_zzard) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                zzb(obj2, com_google_android_gms_internal_zzard);
            }
        }
    }

    protected Object zzck(zzarc com_google_android_gms_internal_zzarc) {
        String valueOf;
        Class componentType = this.bqw ? this.bhd.getComponentType() : this.bhd;
        try {
            zzark com_google_android_gms_internal_zzark;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_zzark = (zzark) componentType.newInstance();
                    com_google_android_gms_internal_zzarc.zza(com_google_android_gms_internal_zzark, zzarn.zzahu(this.tag));
                    return com_google_android_gms_internal_zzark;
                case 11:
                    com_google_android_gms_internal_zzark = (zzark) componentType.newInstance();
                    com_google_android_gms_internal_zzarc.zza(com_google_android_gms_internal_zzark);
                    return com_google_android_gms_internal_zzark;
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

    int zzcu(Object obj) {
        return this.bqw ? zzcv(obj) : zzcw(obj);
    }

    protected int zzcv(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += zzcw(Array.get(obj, i2));
            }
        }
        return i;
    }

    protected int zzcw(Object obj) {
        int zzahu = zzarn.zzahu(this.tag);
        switch (this.type) {
            case 10:
                return zzard.zzb(zzahu, (zzark) obj);
            case 11:
                return zzard.zzc(zzahu, (zzark) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}
