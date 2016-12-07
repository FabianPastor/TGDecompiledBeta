package com.google.android.gms.internal;

import java.io.IOException;

final class zzapj<T> extends zzapk<T> {
    private final zzapg<T> boA;
    private final zzaox<T> boB;
    private final zzaos boC;
    private final zzaqo<T> boD;
    private final zzapl boE;
    private zzapk<T> bol;

    private static class zza implements zzapl {
        private final zzapg<?> boA;
        private final zzaox<?> boB;
        private final zzaqo<?> boF;
        private final boolean boG;
        private final Class<?> boH;

        private zza(Object obj, zzaqo<?> com_google_android_gms_internal_zzaqo_, boolean z, Class<?> cls) {
            this.boA = obj instanceof zzapg ? (zzapg) obj : null;
            this.boB = obj instanceof zzaox ? (zzaox) obj : null;
            boolean z2 = (this.boA == null && this.boB == null) ? false : true;
            zzapq.zzbt(z2);
            this.boF = com_google_android_gms_internal_zzaqo_;
            this.boG = z;
            this.boH = cls;
        }

        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            boolean isAssignableFrom = this.boF != null ? this.boF.equals(com_google_android_gms_internal_zzaqo_T) || (this.boG && this.boF.bC() == com_google_android_gms_internal_zzaqo_T.bB()) : this.boH.isAssignableFrom(com_google_android_gms_internal_zzaqo_T.bB());
            return isAssignableFrom ? new zzapj(this.boA, this.boB, com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzaqo_T, this) : null;
        }
    }

    private zzapj(zzapg<T> com_google_android_gms_internal_zzapg_T, zzaox<T> com_google_android_gms_internal_zzaox_T, zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T, zzapl com_google_android_gms_internal_zzapl) {
        this.boA = com_google_android_gms_internal_zzapg_T;
        this.boB = com_google_android_gms_internal_zzaox_T;
        this.boC = com_google_android_gms_internal_zzaos;
        this.boD = com_google_android_gms_internal_zzaqo_T;
        this.boE = com_google_android_gms_internal_zzapl;
    }

    private zzapk<T> bg() {
        zzapk<T> com_google_android_gms_internal_zzapk_T = this.bol;
        if (com_google_android_gms_internal_zzapk_T != null) {
            return com_google_android_gms_internal_zzapk_T;
        }
        com_google_android_gms_internal_zzapk_T = this.boC.zza(this.boE, this.boD);
        this.bol = com_google_android_gms_internal_zzapk_T;
        return com_google_android_gms_internal_zzapk_T;
    }

    public static zzapl zza(zzaqo<?> com_google_android_gms_internal_zzaqo_, Object obj) {
        return new zza(obj, com_google_android_gms_internal_zzaqo_, false, null);
    }

    public static zzapl zzb(zzaqo<?> com_google_android_gms_internal_zzaqo_, Object obj) {
        return new zza(obj, com_google_android_gms_internal_zzaqo_, com_google_android_gms_internal_zzaqo_.bC() == com_google_android_gms_internal_zzaqo_.bB(), null);
    }

    public void zza(zzaqr com_google_android_gms_internal_zzaqr, T t) throws IOException {
        if (this.boA == null) {
            bg().zza(com_google_android_gms_internal_zzaqr, t);
        } else if (t == null) {
            com_google_android_gms_internal_zzaqr.bA();
        } else {
            zzapz.zzb(this.boA.zza(t, this.boD.bC(), this.boC.boj), com_google_android_gms_internal_zzaqr);
        }
    }

    public T zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        if (this.boB == null) {
            return bg().zzb(com_google_android_gms_internal_zzaqp);
        }
        zzaoy zzh = zzapz.zzh(com_google_android_gms_internal_zzaqp);
        if (zzh.aY()) {
            return null;
        }
        try {
            return this.boB.zzb(zzh, this.boD.bC(), this.boC.boi);
        } catch (zzapc e) {
            throw e;
        } catch (Throwable e2) {
            throw new zzapc(e2);
        }
    }
}
