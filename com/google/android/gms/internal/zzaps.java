package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class zzaps implements zzaou {
    private final zzapb bkM;
    private final zzapc bkV;
    private final zzaoa bkX;

    static abstract class zzb {
        final boolean bmN;
        final boolean bmO;
        final String name;

        protected zzb(String str, boolean z, boolean z2) {
            this.name = str;
            this.bmN = z;
            this.bmO = z2;
        }

        abstract void zza(zzapy com_google_android_gms_internal_zzapy, Object obj) throws IOException, IllegalAccessException;

        abstract void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj) throws IOException, IllegalAccessException;

        abstract boolean zzct(Object obj) throws IOException, IllegalAccessException;
    }

    public static final class zza<T> extends zzaot<T> {
        private final Map<String, zzb> bmM;
        private final zzapg<T> bmt;

        private zza(zzapg<T> com_google_android_gms_internal_zzapg_T, Map<String, zzb> map) {
            this.bmt = com_google_android_gms_internal_zzapg_T;
            this.bmM = map;
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException {
            if (t == null) {
                com_google_android_gms_internal_zzaqa.bx();
                return;
            }
            com_google_android_gms_internal_zzaqa.bv();
            try {
                for (zzb com_google_android_gms_internal_zzaps_zzb : this.bmM.values()) {
                    if (com_google_android_gms_internal_zzaps_zzb.zzct(t)) {
                        com_google_android_gms_internal_zzaqa.zzus(com_google_android_gms_internal_zzaps_zzb.name);
                        com_google_android_gms_internal_zzaps_zzb.zza(com_google_android_gms_internal_zzaqa, (Object) t);
                    }
                }
                com_google_android_gms_internal_zzaqa.bw();
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        public T zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            T bg = this.bmt.bg();
            try {
                com_google_android_gms_internal_zzapy.beginObject();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    zzb com_google_android_gms_internal_zzaps_zzb = (zzb) this.bmM.get(com_google_android_gms_internal_zzapy.nextName());
                    if (com_google_android_gms_internal_zzaps_zzb == null || !com_google_android_gms_internal_zzaps_zzb.bmO) {
                        com_google_android_gms_internal_zzapy.skipValue();
                    } else {
                        com_google_android_gms_internal_zzaps_zzb.zza(com_google_android_gms_internal_zzapy, (Object) bg);
                    }
                }
                com_google_android_gms_internal_zzapy.endObject();
                return bg;
            } catch (Throwable e) {
                throw new zzaoq(e);
            } catch (IllegalAccessException e2) {
                throw new AssertionError(e2);
            }
        }
    }

    public zzaps(zzapb com_google_android_gms_internal_zzapb, zzaoa com_google_android_gms_internal_zzaoa, zzapc com_google_android_gms_internal_zzapc) {
        this.bkM = com_google_android_gms_internal_zzapb;
        this.bkX = com_google_android_gms_internal_zzaoa;
        this.bkV = com_google_android_gms_internal_zzapc;
    }

    private zzaot<?> zza(zzaob com_google_android_gms_internal_zzaob, Field field, zzapx<?> com_google_android_gms_internal_zzapx_) {
        zzaov com_google_android_gms_internal_zzaov = (zzaov) field.getAnnotation(zzaov.class);
        if (com_google_android_gms_internal_zzaov != null) {
            zzaot<?> zza = zzapn.zza(this.bkM, com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzapx_, com_google_android_gms_internal_zzaov);
            if (zza != null) {
                return zza;
            }
        }
        return com_google_android_gms_internal_zzaob.zza((zzapx) com_google_android_gms_internal_zzapx_);
    }

    private zzb zza(zzaob com_google_android_gms_internal_zzaob, Field field, String str, zzapx<?> com_google_android_gms_internal_zzapx_, boolean z, boolean z2) {
        final boolean zzk = zzaph.zzk(com_google_android_gms_internal_zzapx_.by());
        final zzaob com_google_android_gms_internal_zzaob2 = com_google_android_gms_internal_zzaob;
        final Field field2 = field;
        final zzapx<?> com_google_android_gms_internal_zzapx_2 = com_google_android_gms_internal_zzapx_;
        return new zzb(this, str, z, z2) {
            final zzaot<?> bmG = this.bmL.zza(com_google_android_gms_internal_zzaob2, field2, com_google_android_gms_internal_zzapx_2);
            final /* synthetic */ zzaps bmL;

            void zza(zzapy com_google_android_gms_internal_zzapy, Object obj) throws IOException, IllegalAccessException {
                Object zzb = this.bmG.zzb(com_google_android_gms_internal_zzapy);
                if (zzb != null || !zzk) {
                    field2.set(obj, zzb);
                }
            }

            void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj) throws IOException, IllegalAccessException {
                new zzapv(com_google_android_gms_internal_zzaob2, this.bmG, com_google_android_gms_internal_zzapx_2.bz()).zza(com_google_android_gms_internal_zzaqa, field2.get(obj));
            }

            public boolean zzct(Object obj) throws IOException, IllegalAccessException {
                return this.bmN && field2.get(obj) != obj;
            }
        };
    }

    static List<String> zza(zzaoa com_google_android_gms_internal_zzaoa, Field field) {
        zzaow com_google_android_gms_internal_zzaow = (zzaow) field.getAnnotation(zzaow.class);
        List<String> linkedList = new LinkedList();
        if (com_google_android_gms_internal_zzaow == null) {
            linkedList.add(com_google_android_gms_internal_zzaoa.zzc(field));
        } else {
            linkedList.add(com_google_android_gms_internal_zzaow.value());
            for (Object add : com_google_android_gms_internal_zzaow.be()) {
                linkedList.add(add);
            }
        }
        return linkedList;
    }

    private Map<String, zzb> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<?> com_google_android_gms_internal_zzapx_, Class<?> cls) {
        Map<String, zzb> linkedHashMap = new LinkedHashMap();
        if (cls.isInterface()) {
            return linkedHashMap;
        }
        Type bz = com_google_android_gms_internal_zzapx_.bz();
        Class by;
        while (by != Object.class) {
            for (Field field : by.getDeclaredFields()) {
                boolean zza = zza(field, true);
                boolean zza2 = zza(field, false);
                if (zza || zza2) {
                    field.setAccessible(true);
                    Type zza3 = zzapa.zza(r19.bz(), by, field.getGenericType());
                    List zzd = zzd(field);
                    zzb com_google_android_gms_internal_zzaps_zzb = null;
                    int i = 0;
                    while (i < zzd.size()) {
                        String str = (String) zzd.get(i);
                        if (i != 0) {
                            zza = false;
                        }
                        zzb com_google_android_gms_internal_zzaps_zzb2 = (zzb) linkedHashMap.put(str, zza(com_google_android_gms_internal_zzaob, field, str, zzapx.zzl(zza3), zza, zza2));
                        if (com_google_android_gms_internal_zzaps_zzb != null) {
                            com_google_android_gms_internal_zzaps_zzb2 = com_google_android_gms_internal_zzaps_zzb;
                        }
                        i++;
                        com_google_android_gms_internal_zzaps_zzb = com_google_android_gms_internal_zzaps_zzb2;
                    }
                    if (com_google_android_gms_internal_zzaps_zzb != null) {
                        String valueOf = String.valueOf(bz);
                        String str2 = com_google_android_gms_internal_zzaps_zzb.name;
                        throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 37) + String.valueOf(str2).length()).append(valueOf).append(" declares multiple JSON fields named ").append(str2).toString());
                    }
                }
            }
            zzapx zzl = zzapx.zzl(zzapa.zza(zzl.bz(), by, by.getGenericSuperclass()));
            by = zzl.by();
        }
        return linkedHashMap;
    }

    static boolean zza(Field field, boolean z, zzapc com_google_android_gms_internal_zzapc) {
        return (com_google_android_gms_internal_zzapc.zza(field.getType(), z) || com_google_android_gms_internal_zzapc.zza(field, z)) ? false : true;
    }

    private List<String> zzd(Field field) {
        return zza(this.bkX, field);
    }

    public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Class by = com_google_android_gms_internal_zzapx_T.by();
        return !Object.class.isAssignableFrom(by) ? null : new zza(this.bkM.zzb(com_google_android_gms_internal_zzapx_T), zza(com_google_android_gms_internal_zzaob, (zzapx) com_google_android_gms_internal_zzapx_T, by));
    }

    public boolean zza(Field field, boolean z) {
        return zza(field, z, this.bkV);
    }
}
