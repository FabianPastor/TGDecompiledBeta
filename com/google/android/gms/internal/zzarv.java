package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzarv<M extends zzaru<M>, T> {
    protected final Class<T> bkp;
    protected final boolean btH;
    public final int tag;
    protected final int type;

    private zzarv(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.bkp = cls;
        this.tag = i2;
        this.btH = z;
    }

    public static <M extends zzaru<M>, T extends zzasa> zzarv<M, T> zza(int i, Class<T> cls, long j) {
        return new zzarv(i, cls, (int) j, false);
    }

    private T zzaz(List<zzasc> list) {
        int i;
        int i2 = 0;
        List arrayList = new ArrayList();
        for (i = 0; i < list.size(); i++) {
            zzasc com_google_android_gms_internal_zzasc = (zzasc) list.get(i);
            if (com_google_android_gms_internal_zzasc.btQ.length != 0) {
                zza(com_google_android_gms_internal_zzasc, arrayList);
            }
        }
        i = arrayList.size();
        if (i == 0) {
            return null;
        }
        T cast = this.bkp.cast(Array.newInstance(this.bkp.getComponentType(), i));
        while (i2 < i) {
            Array.set(cast, i2, arrayList.get(i2));
            i2++;
        }
        return cast;
    }

    private T zzba(List<zzasc> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.bkp.cast(zzcm(zzars.zzbd(((zzasc) list.get(list.size() - 1)).btQ)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzarv)) {
            return false;
        }
        zzarv com_google_android_gms_internal_zzarv = (zzarv) obj;
        return this.type == com_google_android_gms_internal_zzarv.type && this.bkp == com_google_android_gms_internal_zzarv.bkp && this.tag == com_google_android_gms_internal_zzarv.tag && this.btH == com_google_android_gms_internal_zzarv.btH;
    }

    public int hashCode() {
        return (this.btH ? 1 : 0) + ((((((this.type + 1147) * 31) + this.bkp.hashCode()) * 31) + this.tag) * 31);
    }

    protected void zza(zzasc com_google_android_gms_internal_zzasc, List<Object> list) {
        list.add(zzcm(zzars.zzbd(com_google_android_gms_internal_zzasc.btQ)));
    }

    void zza(Object obj, zzart com_google_android_gms_internal_zzart) throws IOException {
        if (this.btH) {
            zzc(obj, com_google_android_gms_internal_zzart);
        } else {
            zzb(obj, com_google_android_gms_internal_zzart);
        }
    }

    final T zzay(List<zzasc> list) {
        return list == null ? null : this.btH ? zzaz(list) : zzba(list);
    }

    protected void zzb(Object obj, zzart com_google_android_gms_internal_zzart) {
        try {
            com_google_android_gms_internal_zzart.zzahd(this.tag);
            switch (this.type) {
                case 10:
                    zzasa com_google_android_gms_internal_zzasa = (zzasa) obj;
                    int zzahl = zzasd.zzahl(this.tag);
                    com_google_android_gms_internal_zzart.zzb(com_google_android_gms_internal_zzasa);
                    com_google_android_gms_internal_zzart.zzaj(zzahl, 4);
                    return;
                case 11:
                    com_google_android_gms_internal_zzart.zzc((zzasa) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException(e);
    }

    protected void zzc(Object obj, zzart com_google_android_gms_internal_zzart) {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                zzb(obj2, com_google_android_gms_internal_zzart);
            }
        }
    }

    protected Object zzcm(zzars com_google_android_gms_internal_zzars) {
        String valueOf;
        Class componentType = this.btH ? this.bkp.getComponentType() : this.bkp;
        try {
            zzasa com_google_android_gms_internal_zzasa;
            switch (this.type) {
                case 10:
                    com_google_android_gms_internal_zzasa = (zzasa) componentType.newInstance();
                    com_google_android_gms_internal_zzars.zza(com_google_android_gms_internal_zzasa, zzasd.zzahl(this.tag));
                    return com_google_android_gms_internal_zzasa;
                case 11:
                    com_google_android_gms_internal_zzasa = (zzasa) componentType.newInstance();
                    com_google_android_gms_internal_zzars.zza(com_google_android_gms_internal_zzasa);
                    return com_google_android_gms_internal_zzasa;
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

    int zzct(Object obj) {
        return this.btH ? zzcu(obj) : zzcv(obj);
    }

    protected int zzcu(Object obj) {
        int i = 0;
        int length = Array.getLength(obj);
        for (int i2 = 0; i2 < length; i2++) {
            if (Array.get(obj, i2) != null) {
                i += zzcv(Array.get(obj, i2));
            }
        }
        return i;
    }

    protected int zzcv(Object obj) {
        int zzahl = zzasd.zzahl(this.tag);
        switch (this.type) {
            case 10:
                return zzart.zzb(zzahl, (zzasa) obj);
            case 11:
                return zzart.zzc(zzahl, (zzasa) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }
}
