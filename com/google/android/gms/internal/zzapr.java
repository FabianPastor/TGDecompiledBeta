package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class zzapr extends zzaot<Object> {
    public static final zzaou bmp = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Object.class ? new zzapr(com_google_android_gms_internal_zzaob) : null;
        }
    };
    private final zzaob bll;

    private zzapr(zzaob com_google_android_gms_internal_zzaob) {
        this.bll = com_google_android_gms_internal_zzaob;
    }

    public void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj) throws IOException {
        if (obj == null) {
            com_google_android_gms_internal_zzaqa.bx();
            return;
        }
        zzaot zzk = this.bll.zzk(obj.getClass());
        if (zzk instanceof zzapr) {
            com_google_android_gms_internal_zzaqa.bv();
            com_google_android_gms_internal_zzaqa.bw();
            return;
        }
        zzk.zza(com_google_android_gms_internal_zzaqa, obj);
    }

    public Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
        switch (com_google_android_gms_internal_zzapy.bn()) {
            case BEGIN_ARRAY:
                List arrayList = new ArrayList();
                com_google_android_gms_internal_zzapy.beginArray();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    arrayList.add(zzb(com_google_android_gms_internal_zzapy));
                }
                com_google_android_gms_internal_zzapy.endArray();
                return arrayList;
            case BEGIN_OBJECT:
                Map com_google_android_gms_internal_zzapf = new zzapf();
                com_google_android_gms_internal_zzapy.beginObject();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    com_google_android_gms_internal_zzapf.put(com_google_android_gms_internal_zzapy.nextName(), zzb(com_google_android_gms_internal_zzapy));
                }
                com_google_android_gms_internal_zzapy.endObject();
                return com_google_android_gms_internal_zzapf;
            case STRING:
                return com_google_android_gms_internal_zzapy.nextString();
            case NUMBER:
                return Double.valueOf(com_google_android_gms_internal_zzapy.nextDouble());
            case BOOLEAN:
                return Boolean.valueOf(com_google_android_gms_internal_zzapy.nextBoolean());
            case NULL:
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
