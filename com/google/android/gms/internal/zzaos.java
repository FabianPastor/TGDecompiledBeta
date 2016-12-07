package com.google.android.gms.internal;

import java.io.IOException;

final class zzaos<T> extends zzaot<T> {
    private zzaot<T> bkU;
    private final zzaop<T> blj;
    private final zzaog<T> blk;
    private final zzaob bll;
    private final zzapx<T> blm;
    private final zzaou bln;

    private static class zza implements zzaou {
        private final zzaop<?> blj;
        private final zzaog<?> blk;
        private final zzapx<?> blo;
        private final boolean blp;
        private final Class<?> blq;

        private zza(Object obj, zzapx<?> com_google_android_gms_internal_zzapx_, boolean z, Class<?> cls) {
            this.blj = obj instanceof zzaop ? (zzaop) obj : null;
            this.blk = obj instanceof zzaog ? (zzaog) obj : null;
            boolean z2 = (this.blj == null && this.blk == null) ? false : true;
            zzaoz.zzbs(z2);
            this.blo = com_google_android_gms_internal_zzapx_;
            this.blp = z;
            this.blq = cls;
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            boolean isAssignableFrom = this.blo != null ? this.blo.equals(com_google_android_gms_internal_zzapx_T) || (this.blp && this.blo.bz() == com_google_android_gms_internal_zzapx_T.by()) : this.blq.isAssignableFrom(com_google_android_gms_internal_zzapx_T.by());
            return isAssignableFrom ? new zzaos(this.blj, this.blk, com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzapx_T, this) : null;
        }
    }

    private zzaos(zzaop<T> com_google_android_gms_internal_zzaop_T, zzaog<T> com_google_android_gms_internal_zzaog_T, zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T, zzaou com_google_android_gms_internal_zzaou) {
        this.blj = com_google_android_gms_internal_zzaop_T;
        this.blk = com_google_android_gms_internal_zzaog_T;
        this.bll = com_google_android_gms_internal_zzaob;
        this.blm = com_google_android_gms_internal_zzapx_T;
        this.bln = com_google_android_gms_internal_zzaou;
    }

    private zzaot<T> bd() {
        zzaot<T> com_google_android_gms_internal_zzaot_T = this.bkU;
        if (com_google_android_gms_internal_zzaot_T != null) {
            return com_google_android_gms_internal_zzaot_T;
        }
        com_google_android_gms_internal_zzaot_T = this.bll.zza(this.bln, this.blm);
        this.bkU = com_google_android_gms_internal_zzaot_T;
        return com_google_android_gms_internal_zzaot_T;
    }

    public static zzaou zza(zzapx<?> com_google_android_gms_internal_zzapx_, Object obj) {
        return new zza(obj, com_google_android_gms_internal_zzapx_, false, null);
    }

    public static zzaou zzb(zzapx<?> com_google_android_gms_internal_zzapx_, Object obj) {
        return new zza(obj, com_google_android_gms_internal_zzapx_, com_google_android_gms_internal_zzapx_.bz() == com_google_android_gms_internal_zzapx_.by(), null);
    }

    public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException {
        if (this.blj == null) {
            bd().zza(com_google_android_gms_internal_zzaqa, t);
        } else if (t == null) {
            com_google_android_gms_internal_zzaqa.bx();
        } else {
            zzapi.zzb(this.blj.zza(t, this.blm.bz(), this.bll.bkS), com_google_android_gms_internal_zzaqa);
        }
    }

    public T zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        if (this.blk == null) {
            return bd().zzb(com_google_android_gms_internal_zzapy);
        }
        zzaoh zzh = zzapi.zzh(com_google_android_gms_internal_zzapy);
        if (zzh.aV()) {
            return null;
        }
        try {
            return this.blk.zzb(zzh, this.blm.bz(), this.bll.bkR);
        } catch (zzaol e) {
            throw e;
        } catch (Throwable e2) {
            throw new zzaol(e2);
        }
    }
}
