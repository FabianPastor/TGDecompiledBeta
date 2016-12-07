package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class zzaqh implements zzapl {
    private final zzaps bod;
    private final boolean bpS;

    private final class zza<K, V> extends zzapk<Map<K, V>> {
        private final zzapx<? extends Map<K, V>> bpK;
        private final zzapk<K> bpT;
        private final zzapk<V> bpU;
        final /* synthetic */ zzaqh bpV;

        public zza(zzaqh com_google_android_gms_internal_zzaqh, zzaos com_google_android_gms_internal_zzaos, Type type, zzapk<K> com_google_android_gms_internal_zzapk_K, Type type2, zzapk<V> com_google_android_gms_internal_zzapk_V, zzapx<? extends Map<K, V>> com_google_android_gms_internal_zzapx__extends_java_util_Map_K__V) {
            this.bpV = com_google_android_gms_internal_zzaqh;
            this.bpT = new zzaqm(com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzapk_K, type);
            this.bpU = new zzaqm(com_google_android_gms_internal_zzaos, com_google_android_gms_internal_zzapk_V, type2);
            this.bpK = com_google_android_gms_internal_zzapx__extends_java_util_Map_K__V;
        }

        private String zze(zzaoy com_google_android_gms_internal_zzaoy) {
            if (com_google_android_gms_internal_zzaoy.aX()) {
                zzape bb = com_google_android_gms_internal_zzaoy.bb();
                if (bb.be()) {
                    return String.valueOf(bb.aT());
                }
                if (bb.bd()) {
                    return Boolean.toString(bb.getAsBoolean());
                }
                if (bb.bf()) {
                    return bb.aU();
                }
                throw new AssertionError();
            } else if (com_google_android_gms_internal_zzaoy.aY()) {
                return "null";
            } else {
                throw new AssertionError();
            }
        }

        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Map<K, V> map) throws IOException {
            int i = 0;
            if (map == null) {
                com_google_android_gms_internal_zzaqr.bA();
            } else if (this.bpV.bpS) {
                List arrayList = new ArrayList(map.size());
                List arrayList2 = new ArrayList(map.size());
                int i2 = 0;
                for (Entry entry : map.entrySet()) {
                    zzaoy zzcn = this.bpT.zzcn(entry.getKey());
                    arrayList.add(zzcn);
                    arrayList2.add(entry.getValue());
                    int i3 = (zzcn.aV() || zzcn.aW()) ? 1 : 0;
                    i2 = i3 | i2;
                }
                if (i2 != 0) {
                    com_google_android_gms_internal_zzaqr.bw();
                    while (i < arrayList.size()) {
                        com_google_android_gms_internal_zzaqr.bw();
                        zzapz.zzb((zzaoy) arrayList.get(i), com_google_android_gms_internal_zzaqr);
                        this.bpU.zza(com_google_android_gms_internal_zzaqr, arrayList2.get(i));
                        com_google_android_gms_internal_zzaqr.bx();
                        i++;
                    }
                    com_google_android_gms_internal_zzaqr.bx();
                    return;
                }
                com_google_android_gms_internal_zzaqr.by();
                while (i < arrayList.size()) {
                    com_google_android_gms_internal_zzaqr.zzus(zze((zzaoy) arrayList.get(i)));
                    this.bpU.zza(com_google_android_gms_internal_zzaqr, arrayList2.get(i));
                    i++;
                }
                com_google_android_gms_internal_zzaqr.bz();
            } else {
                com_google_android_gms_internal_zzaqr.by();
                for (Entry entry2 : map.entrySet()) {
                    com_google_android_gms_internal_zzaqr.zzus(String.valueOf(entry2.getKey()));
                    this.bpU.zza(com_google_android_gms_internal_zzaqr, entry2.getValue());
                }
                com_google_android_gms_internal_zzaqr.bz();
            }
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzl(com_google_android_gms_internal_zzaqp);
        }

        public Map<K, V> zzl(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            zzaqq bq = com_google_android_gms_internal_zzaqp.bq();
            if (bq == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            Map<K, V> map = (Map) this.bpK.bj();
            Object zzb;
            if (bq == zzaqq.BEGIN_ARRAY) {
                com_google_android_gms_internal_zzaqp.beginArray();
                while (com_google_android_gms_internal_zzaqp.hasNext()) {
                    com_google_android_gms_internal_zzaqp.beginArray();
                    zzb = this.bpT.zzb(com_google_android_gms_internal_zzaqp);
                    if (map.put(zzb, this.bpU.zzb(com_google_android_gms_internal_zzaqp)) != null) {
                        String valueOf = String.valueOf(zzb);
                        throw new zzaph(new StringBuilder(String.valueOf(valueOf).length() + 15).append("duplicate key: ").append(valueOf).toString());
                    }
                    com_google_android_gms_internal_zzaqp.endArray();
                }
                com_google_android_gms_internal_zzaqp.endArray();
                return map;
            }
            com_google_android_gms_internal_zzaqp.beginObject();
            while (com_google_android_gms_internal_zzaqp.hasNext()) {
                zzapu.bph.zzi(com_google_android_gms_internal_zzaqp);
                zzb = this.bpT.zzb(com_google_android_gms_internal_zzaqp);
                if (map.put(zzb, this.bpU.zzb(com_google_android_gms_internal_zzaqp)) != null) {
                    valueOf = String.valueOf(zzb);
                    throw new zzaph(new StringBuilder(String.valueOf(valueOf).length() + 15).append("duplicate key: ").append(valueOf).toString());
                }
            }
            com_google_android_gms_internal_zzaqp.endObject();
            return map;
        }
    }

    public zzaqh(zzaps com_google_android_gms_internal_zzaps, boolean z) {
        this.bod = com_google_android_gms_internal_zzaps;
        this.bpS = z;
    }

    private zzapk<?> zza(zzaos com_google_android_gms_internal_zzaos, Type type) {
        return (type == Boolean.TYPE || type == Boolean.class) ? zzaqn.bqo : com_google_android_gms_internal_zzaos.zza(zzaqo.zzl(type));
    }

    public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        Type bC = com_google_android_gms_internal_zzaqo_T.bC();
        if (!Map.class.isAssignableFrom(com_google_android_gms_internal_zzaqo_T.bB())) {
            return null;
        }
        Type[] zzb = zzapr.zzb(bC, zzapr.zzf(bC));
        zzapk zza = zza(com_google_android_gms_internal_zzaos, zzb[0]);
        zzapk zza2 = com_google_android_gms_internal_zzaos.zza(zzaqo.zzl(zzb[1]));
        zzapx zzb2 = this.bod.zzb(com_google_android_gms_internal_zzaqo_T);
        return new zza(this, com_google_android_gms_internal_zzaos, zzb[0], zza, zzb[1], zza2, zzb2);
    }
}
