package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class zzaqj implements zzapl {
    private final zzaps bod;
    private final zzapt bom;
    private final zzaor boo;

    static abstract class zzb {
        final boolean bqe;
        final boolean bqf;
        final String name;

        protected zzb(String str, boolean z, boolean z2) {
            this.name = str;
            this.bqe = z;
            this.bqf = z2;
        }

        abstract void zza(zzaqp com_google_android_gms_internal_zzaqp, Object obj) throws IOException, IllegalAccessException;

        abstract void zza(zzaqr com_google_android_gms_internal_zzaqr, Object obj) throws IOException, IllegalAccessException;

        abstract boolean zzcs(Object obj) throws IOException, IllegalAccessException;
    }

    public static final class zza<T> extends zzapk<T> {
        private final zzapx<T> bpK;
        private final Map<String, zzb> bqd;

        private zza(zzapx<T> com_google_android_gms_internal_zzapx_T, Map<String, zzb> map) {
            this.bpK = com_google_android_gms_internal_zzapx_T;
            this.bqd = map;
        }

        public void zza(zzaqr com_google_android_gms_internal_zzaqr, T t) throws IOException {
            if (t == null) {
                com_google_android_gms_internal_zzaqr.bA();
                return;
            }
            com_google_android_gms_internal_zzaqr.by();
            try {
                for (zzb com_google_android_gms_internal_zzaqj_zzb : this.bqd.values()) {
                    if (com_google_android_gms_internal_zzaqj_zzb.zzcs(t)) {
                        com_google_android_gms_internal_zzaqr.zzus(com_google_android_gms_internal_zzaqj_zzb.name);
                        com_google_android_gms_internal_zzaqj_zzb.zza(com_google_android_gms_internal_zzaqr, (Object) t);
                    }
                }
                com_google_android_gms_internal_zzaqr.bz();
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        public T zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            T bj = this.bpK.bj();
            try {
                com_google_android_gms_internal_zzaqp.beginObject();
                while (com_google_android_gms_internal_zzaqp.hasNext()) {
                    zzb com_google_android_gms_internal_zzaqj_zzb = (zzb) this.bqd.get(com_google_android_gms_internal_zzaqp.nextName());
                    if (com_google_android_gms_internal_zzaqj_zzb == null || !com_google_android_gms_internal_zzaqj_zzb.bqf) {
                        com_google_android_gms_internal_zzaqp.skipValue();
                    } else {
                        com_google_android_gms_internal_zzaqj_zzb.zza(com_google_android_gms_internal_zzaqp, (Object) bj);
                    }
                }
                com_google_android_gms_internal_zzaqp.endObject();
                return bj;
            } catch (Throwable e) {
                throw new zzaph(e);
            } catch (IllegalAccessException e2) {
                throw new AssertionError(e2);
            }
        }
    }

    public zzaqj(zzaps com_google_android_gms_internal_zzaps, zzaor com_google_android_gms_internal_zzaor, zzapt com_google_android_gms_internal_zzapt) {
        this.bod = com_google_android_gms_internal_zzaps;
        this.boo = com_google_android_gms_internal_zzaor;
        this.bom = com_google_android_gms_internal_zzapt;
    }

    private zzapk<?> zza(zzaos com_google_android_gms_internal_zzaos, Field field, zzaqo<?> com_google_android_gms_internal_zzaqo_) {
        zzapm com_google_android_gms_internal_zzapm = (zzapm) field.getAnnotation(zzapm.class);
        if (com_google_android_gms_internal_zzapm != null) {
            zzapk<?> zza = zzaqe.zza(this.bod, com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzaqo_, com_google_android_gms_internal_zzapm);
            if (zza != null) {
                return zza;
            }
        }
        return com_google_android_gms_internal_zzaos.zza((zzaqo) com_google_android_gms_internal_zzaqo_);
    }

    private zzb zza(zzaos com_google_android_gms_internal_zzaos, Field field, String str, zzaqo<?> com_google_android_gms_internal_zzaqo_, boolean z, boolean z2) {
        final boolean zzk = zzapy.zzk(com_google_android_gms_internal_zzaqo_.bB());
        final zzaos com_google_android_gms_internal_zzaos2 = com_google_android_gms_internal_zzaos;
        final Field field2 = field;
        final zzaqo<?> com_google_android_gms_internal_zzaqo_2 = com_google_android_gms_internal_zzaqo_;
        return new zzb(this, str, z, z2) {
            final zzapk<?> bpX = this.bqc.zza(com_google_android_gms_internal_zzaos2, field2, com_google_android_gms_internal_zzaqo_2);
            final /* synthetic */ zzaqj bqc;

            void zza(zzaqp com_google_android_gms_internal_zzaqp, Object obj) throws IOException, IllegalAccessException {
                Object zzb = this.bpX.zzb(com_google_android_gms_internal_zzaqp);
                if (zzb != null || !zzk) {
                    field2.set(obj, zzb);
                }
            }

            void zza(zzaqr com_google_android_gms_internal_zzaqr, Object obj) throws IOException, IllegalAccessException {
                new zzaqm(com_google_android_gms_internal_zzaos2, this.bpX, com_google_android_gms_internal_zzaqo_2.bC()).zza(com_google_android_gms_internal_zzaqr, field2.get(obj));
            }

            public boolean zzcs(Object obj) throws IOException, IllegalAccessException {
                return this.bqe && field2.get(obj) != obj;
            }
        };
    }

    static List<String> zza(zzaor com_google_android_gms_internal_zzaor, Field field) {
        zzapn com_google_android_gms_internal_zzapn = (zzapn) field.getAnnotation(zzapn.class);
        List<String> linkedList = new LinkedList();
        if (com_google_android_gms_internal_zzapn == null) {
            linkedList.add(com_google_android_gms_internal_zzaor.zzc(field));
        } else {
            linkedList.add(com_google_android_gms_internal_zzapn.value());
            for (Object add : com_google_android_gms_internal_zzapn.bh()) {
                linkedList.add(add);
            }
        }
        return linkedList;
    }

    private Map<String, zzb> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<?> com_google_android_gms_internal_zzaqo_, Class<?> cls) {
        Map<String, zzb> linkedHashMap = new LinkedHashMap();
        if (cls.isInterface()) {
            return linkedHashMap;
        }
        Type bC = com_google_android_gms_internal_zzaqo_.bC();
        Class bB;
        while (bB != Object.class) {
            for (Field field : bB.getDeclaredFields()) {
                boolean zza = zza(field, true);
                boolean zza2 = zza(field, false);
                if (zza || zza2) {
                    field.setAccessible(true);
                    Type zza3 = zzapr.zza(r19.bC(), bB, field.getGenericType());
                    List zzd = zzd(field);
                    zzb com_google_android_gms_internal_zzaqj_zzb = null;
                    int i = 0;
                    while (i < zzd.size()) {
                        String str = (String) zzd.get(i);
                        if (i != 0) {
                            zza = false;
                        }
                        zzb com_google_android_gms_internal_zzaqj_zzb2 = (zzb) linkedHashMap.put(str, zza(com_google_android_gms_internal_zzaos, field, str, zzaqo.zzl(zza3), zza, zza2));
                        if (com_google_android_gms_internal_zzaqj_zzb != null) {
                            com_google_android_gms_internal_zzaqj_zzb2 = com_google_android_gms_internal_zzaqj_zzb;
                        }
                        i++;
                        com_google_android_gms_internal_zzaqj_zzb = com_google_android_gms_internal_zzaqj_zzb2;
                    }
                    if (com_google_android_gms_internal_zzaqj_zzb != null) {
                        String valueOf = String.valueOf(bC);
                        String str2 = com_google_android_gms_internal_zzaqj_zzb.name;
                        throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 37) + String.valueOf(str2).length()).append(valueOf).append(" declares multiple JSON fields named ").append(str2).toString());
                    }
                }
            }
            zzaqo zzl = zzaqo.zzl(zzapr.zza(zzl.bC(), bB, bB.getGenericSuperclass()));
            bB = zzl.bB();
        }
        return linkedHashMap;
    }

    static boolean zza(Field field, boolean z, zzapt com_google_android_gms_internal_zzapt) {
        return (com_google_android_gms_internal_zzapt.zza(field.getType(), z) || com_google_android_gms_internal_zzapt.zza(field, z)) ? false : true;
    }

    private List<String> zzd(Field field) {
        return zza(this.boo, field);
    }

    public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        Class bB = com_google_android_gms_internal_zzaqo_T.bB();
        return !Object.class.isAssignableFrom(bB) ? null : new zza(this.bod.zzb(com_google_android_gms_internal_zzaqo_T), zza(com_google_android_gms_internal_zzaos, (zzaqo) com_google_android_gms_internal_zzaqo_T, bB));
    }

    public boolean zza(Field field, boolean z) {
        return zza(field, z, this.bom);
    }
}
