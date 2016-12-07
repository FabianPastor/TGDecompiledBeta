package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public final class zzaqc implements zzapl {
    private final zzaps bod;

    private static final class zza<E> extends zzapk<Collection<E>> {
        private final zzapk<E> bpJ;
        private final zzapx<? extends Collection<E>> bpK;

        public zza(zzaos com_google_android_gms_internal_zzaos, Type type, zzapk<E> com_google_android_gms_internal_zzapk_E, zzapx<? extends Collection<E>> com_google_android_gms_internal_zzapx__extends_java_util_Collection_E) {
            this.bpJ = new zzaqm(com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzapk_E, type);
            this.bpK = com_google_android_gms_internal_zzapx__extends_java_util_Collection_E;
        }

        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Collection<E> collection) throws IOException {
            if (collection == null) {
                com_google_android_gms_internal_zzaqr.bA();
                return;
            }
            com_google_android_gms_internal_zzaqr.bw();
            for (E zza : collection) {
                this.bpJ.zza(com_google_android_gms_internal_zzaqr, zza);
            }
            com_google_android_gms_internal_zzaqr.bx();
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzj(com_google_android_gms_internal_zzaqp);
        }

        public Collection<E> zzj(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            Collection<E> collection = (Collection) this.bpK.bj();
            com_google_android_gms_internal_zzaqp.beginArray();
            while (com_google_android_gms_internal_zzaqp.hasNext()) {
                collection.add(this.bpJ.zzb(com_google_android_gms_internal_zzaqp));
            }
            com_google_android_gms_internal_zzaqp.endArray();
            return collection;
        }
    }

    public zzaqc(zzaps com_google_android_gms_internal_zzaps) {
        this.bod = com_google_android_gms_internal_zzaps;
    }

    public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        Type bC = com_google_android_gms_internal_zzaqo_T.bC();
        Class bB = com_google_android_gms_internal_zzaqo_T.bB();
        if (!Collection.class.isAssignableFrom(bB)) {
            return null;
        }
        Type zza = zzapr.zza(bC, bB);
        return new zza(com_google_android_gms_internal_zzaos, zza, com_google_android_gms_internal_zzaos.zza(zzaqo.zzl(zza)), this.bod.zzb(com_google_android_gms_internal_zzaqo_T));
    }
}
