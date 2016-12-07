package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public final class zzapl implements zzaou {
    private final zzapb bkM;

    private static final class zza<E> extends zzaot<Collection<E>> {
        private final zzaot<E> bms;
        private final zzapg<? extends Collection<E>> bmt;

        public zza(zzaob com_google_android_gms_internal_zzaob, Type type, zzaot<E> com_google_android_gms_internal_zzaot_E, zzapg<? extends Collection<E>> com_google_android_gms_internal_zzapg__extends_java_util_Collection_E) {
            this.bms = new zzapv(com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzaot_E, type);
            this.bmt = com_google_android_gms_internal_zzapg__extends_java_util_Collection_E;
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Collection<E> collection) throws IOException {
            if (collection == null) {
                com_google_android_gms_internal_zzaqa.bx();
                return;
            }
            com_google_android_gms_internal_zzaqa.bt();
            for (E zza : collection) {
                this.bms.zza(com_google_android_gms_internal_zzaqa, zza);
            }
            com_google_android_gms_internal_zzaqa.bu();
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzj(com_google_android_gms_internal_zzapy);
        }

        public Collection<E> zzj(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            Collection<E> collection = (Collection) this.bmt.bg();
            com_google_android_gms_internal_zzapy.beginArray();
            while (com_google_android_gms_internal_zzapy.hasNext()) {
                collection.add(this.bms.zzb(com_google_android_gms_internal_zzapy));
            }
            com_google_android_gms_internal_zzapy.endArray();
            return collection;
        }
    }

    public zzapl(zzapb com_google_android_gms_internal_zzapb) {
        this.bkM = com_google_android_gms_internal_zzapb;
    }

    public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Type bz = com_google_android_gms_internal_zzapx_T.bz();
        Class by = com_google_android_gms_internal_zzapx_T.by();
        if (!Collection.class.isAssignableFrom(by)) {
            return null;
        }
        Type zza = zzapa.zza(bz, by);
        return new zza(com_google_android_gms_internal_zzaob, zza, com_google_android_gms_internal_zzaob.zza(zzapx.zzl(zza)), this.bkM.zzb(com_google_android_gms_internal_zzapx_T));
    }
}
