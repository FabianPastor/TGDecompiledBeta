package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class zzapc implements zzaou, Cloneable {
    public static final zzapc blF = new zzapc();
    private double blG = -1.0d;
    private int blH = 136;
    private boolean blI = true;
    private List<zzanx> blJ = Collections.emptyList();
    private List<zzanx> blK = Collections.emptyList();

    private boolean zza(zzaox com_google_android_gms_internal_zzaox) {
        return com_google_android_gms_internal_zzaox == null || com_google_android_gms_internal_zzaox.bf() <= this.blG;
    }

    private boolean zza(zzaox com_google_android_gms_internal_zzaox, zzaoy com_google_android_gms_internal_zzaoy) {
        return zza(com_google_android_gms_internal_zzaox) && zza(com_google_android_gms_internal_zzaoy);
    }

    private boolean zza(zzaoy com_google_android_gms_internal_zzaoy) {
        return com_google_android_gms_internal_zzaoy == null || com_google_android_gms_internal_zzaoy.bf() > this.blG;
    }

    private boolean zzm(Class<?> cls) {
        return !Enum.class.isAssignableFrom(cls) && (cls.isAnonymousClass() || cls.isLocalClass());
    }

    private boolean zzn(Class<?> cls) {
        return cls.isMemberClass() && !zzo(cls);
    }

    private boolean zzo(Class<?> cls) {
        return (cls.getModifiers() & 8) != 0;
    }

    protected zzapc bh() {
        try {
            return (zzapc) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    protected /* synthetic */ Object clone() throws CloneNotSupportedException {
        return bh();
    }

    public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Class by = com_google_android_gms_internal_zzapx_T.by();
        final boolean zza = zza(by, true);
        final boolean zza2 = zza(by, false);
        if (!zza && !zza2) {
            return null;
        }
        final zzaob com_google_android_gms_internal_zzaob2 = com_google_android_gms_internal_zzaob;
        final zzapx<T> com_google_android_gms_internal_zzapx_T2 = com_google_android_gms_internal_zzapx_T;
        return new zzaot<T>(this) {
            private zzaot<T> bkU;
            final /* synthetic */ zzapc blP;

            private zzaot<T> bd() {
                zzaot<T> com_google_android_gms_internal_zzaot_T = this.bkU;
                if (com_google_android_gms_internal_zzaot_T != null) {
                    return com_google_android_gms_internal_zzaot_T;
                }
                com_google_android_gms_internal_zzaot_T = com_google_android_gms_internal_zzaob2.zza(this.blP, com_google_android_gms_internal_zzapx_T2);
                this.bkU = com_google_android_gms_internal_zzaot_T;
                return com_google_android_gms_internal_zzaot_T;
            }

            public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException {
                if (zza) {
                    com_google_android_gms_internal_zzaqa.bx();
                } else {
                    bd().zza(com_google_android_gms_internal_zzaqa, t);
                }
            }

            public T zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                if (!zza2) {
                    return bd().zzb(com_google_android_gms_internal_zzapy);
                }
                com_google_android_gms_internal_zzapy.skipValue();
                return null;
            }
        };
    }

    public zzapc zza(zzanx com_google_android_gms_internal_zzanx, boolean z, boolean z2) {
        zzapc bh = bh();
        if (z) {
            bh.blJ = new ArrayList(this.blJ);
            bh.blJ.add(com_google_android_gms_internal_zzanx);
        }
        if (z2) {
            bh.blK = new ArrayList(this.blK);
            bh.blK.add(com_google_android_gms_internal_zzanx);
        }
        return bh;
    }

    public boolean zza(Class<?> cls, boolean z) {
        if (this.blG != -1.0d && !zza((zzaox) cls.getAnnotation(zzaox.class), (zzaoy) cls.getAnnotation(zzaoy.class))) {
            return true;
        }
        if (!this.blI && zzn(cls)) {
            return true;
        }
        if (zzm(cls)) {
            return true;
        }
        for (zzanx zzh : z ? this.blJ : this.blK) {
            if (zzh.zzh(cls)) {
                return true;
            }
        }
        return false;
    }

    public boolean zza(Field field, boolean z) {
        if ((this.blH & field.getModifiers()) != 0) {
            return true;
        }
        if (this.blG != -1.0d && !zza((zzaox) field.getAnnotation(zzaox.class), (zzaoy) field.getAnnotation(zzaoy.class))) {
            return true;
        }
        if (field.isSynthetic()) {
            return true;
        }
        if (!this.blI && zzn(field.getType())) {
            return true;
        }
        if (zzm(field.getType())) {
            return true;
        }
        List<zzanx> list = z ? this.blJ : this.blK;
        if (!list.isEmpty()) {
            zzany com_google_android_gms_internal_zzany = new zzany(field);
            for (zzanx zza : list) {
                if (zza.zza(com_google_android_gms_internal_zzany)) {
                    return true;
                }
            }
        }
        return false;
    }

    public zzapc zzg(int... iArr) {
        int i = 0;
        zzapc bh = bh();
        bh.blH = 0;
        int length = iArr.length;
        while (i < length) {
            bh.blH = iArr[i] | bh.blH;
            i++;
        }
        return bh;
    }
}
