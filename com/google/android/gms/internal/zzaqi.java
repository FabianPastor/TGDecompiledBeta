package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class zzaqi extends zzapk<Object> {
    public static final zzapl bpG = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            return com_google_android_gms_internal_zzaqo_T.bB() == Object.class ? new zzaqi(com_google_android_gms_internal_zzaos) : null;
        }
    };
    private final zzaos boC;

    private zzaqi(zzaos com_google_android_gms_internal_zzaos) {
        this.boC = com_google_android_gms_internal_zzaos;
    }

    public void zza(zzaqr com_google_android_gms_internal_zzaqr, Object obj) throws IOException {
        if (obj == null) {
            com_google_android_gms_internal_zzaqr.bA();
            return;
        }
        zzapk zzk = this.boC.zzk(obj.getClass());
        if (zzk instanceof zzaqi) {
            com_google_android_gms_internal_zzaqr.by();
            com_google_android_gms_internal_zzaqr.bz();
            return;
        }
        zzk.zza(com_google_android_gms_internal_zzaqr, obj);
    }

    public Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
        switch (com_google_android_gms_internal_zzaqp.bq()) {
            case BEGIN_ARRAY:
                List arrayList = new ArrayList();
                com_google_android_gms_internal_zzaqp.beginArray();
                while (com_google_android_gms_internal_zzaqp.hasNext()) {
                    arrayList.add(zzb(com_google_android_gms_internal_zzaqp));
                }
                com_google_android_gms_internal_zzaqp.endArray();
                return arrayList;
            case BEGIN_OBJECT:
                Map com_google_android_gms_internal_zzapw = new zzapw();
                com_google_android_gms_internal_zzaqp.beginObject();
                while (com_google_android_gms_internal_zzaqp.hasNext()) {
                    com_google_android_gms_internal_zzapw.put(com_google_android_gms_internal_zzaqp.nextName(), zzb(com_google_android_gms_internal_zzaqp));
                }
                com_google_android_gms_internal_zzaqp.endObject();
                return com_google_android_gms_internal_zzapw;
            case STRING:
                return com_google_android_gms_internal_zzaqp.nextString();
            case NUMBER:
                return Double.valueOf(com_google_android_gms_internal_zzaqp.nextDouble());
            case BOOLEAN:
                return Boolean.valueOf(com_google_android_gms_internal_zzaqp.nextBoolean());
            case NULL:
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
