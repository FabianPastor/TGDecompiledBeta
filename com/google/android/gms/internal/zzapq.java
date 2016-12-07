package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class zzapq implements zzaou {
    private final zzapb bkM;
    private final boolean bmB;

    private final class zza<K, V> extends zzaot<Map<K, V>> {
        private final zzaot<K> bmC;
        private final zzaot<V> bmD;
        final /* synthetic */ zzapq bmE;
        private final zzapg<? extends Map<K, V>> bmt;

        public zza(zzapq com_google_android_gms_internal_zzapq, zzaob com_google_android_gms_internal_zzaob, Type type, zzaot<K> com_google_android_gms_internal_zzaot_K, Type type2, zzaot<V> com_google_android_gms_internal_zzaot_V, zzapg<? extends Map<K, V>> com_google_android_gms_internal_zzapg__extends_java_util_Map_K__V) {
            this.bmE = com_google_android_gms_internal_zzapq;
            this.bmC = new zzapv(com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzaot_K, type);
            this.bmD = new zzapv(com_google_android_gms_internal_zzaob, com_google_android_gms_internal_zzaot_V, type2);
            this.bmt = com_google_android_gms_internal_zzapg__extends_java_util_Map_K__V;
        }

        private String zze(zzaoh com_google_android_gms_internal_zzaoh) {
            if (com_google_android_gms_internal_zzaoh.aU()) {
                zzaon aY = com_google_android_gms_internal_zzaoh.aY();
                if (aY.bb()) {
                    return String.valueOf(aY.aQ());
                }
                if (aY.ba()) {
                    return Boolean.toString(aY.getAsBoolean());
                }
                if (aY.bc()) {
                    return aY.aR();
                }
                throw new AssertionError();
            } else if (com_google_android_gms_internal_zzaoh.aV()) {
                return "null";
            } else {
                throw new AssertionError();
            }
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Map<K, V> map) throws IOException {
            int i = 0;
            if (map == null) {
                com_google_android_gms_internal_zzaqa.bx();
            } else if (this.bmE.bmB) {
                List arrayList = new ArrayList(map.size());
                List arrayList2 = new ArrayList(map.size());
                int i2 = 0;
                for (Entry entry : map.entrySet()) {
                    zzaoh zzco = this.bmC.zzco(entry.getKey());
                    arrayList.add(zzco);
                    arrayList2.add(entry.getValue());
                    int i3 = (zzco.aS() || zzco.aT()) ? 1 : 0;
                    i2 = i3 | i2;
                }
                if (i2 != 0) {
                    com_google_android_gms_internal_zzaqa.bt();
                    while (i < arrayList.size()) {
                        com_google_android_gms_internal_zzaqa.bt();
                        zzapi.zzb((zzaoh) arrayList.get(i), com_google_android_gms_internal_zzaqa);
                        this.bmD.zza(com_google_android_gms_internal_zzaqa, arrayList2.get(i));
                        com_google_android_gms_internal_zzaqa.bu();
                        i++;
                    }
                    com_google_android_gms_internal_zzaqa.bu();
                    return;
                }
                com_google_android_gms_internal_zzaqa.bv();
                while (i < arrayList.size()) {
                    com_google_android_gms_internal_zzaqa.zzus(zze((zzaoh) arrayList.get(i)));
                    this.bmD.zza(com_google_android_gms_internal_zzaqa, arrayList2.get(i));
                    i++;
                }
                com_google_android_gms_internal_zzaqa.bw();
            } else {
                com_google_android_gms_internal_zzaqa.bv();
                for (Entry entry2 : map.entrySet()) {
                    com_google_android_gms_internal_zzaqa.zzus(String.valueOf(entry2.getKey()));
                    this.bmD.zza(com_google_android_gms_internal_zzaqa, entry2.getValue());
                }
                com_google_android_gms_internal_zzaqa.bw();
            }
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzl(com_google_android_gms_internal_zzapy);
        }

        public Map<K, V> zzl(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            zzapz bn = com_google_android_gms_internal_zzapy.bn();
            if (bn == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            Map<K, V> map = (Map) this.bmt.bg();
            Object zzb;
            if (bn == zzapz.BEGIN_ARRAY) {
                com_google_android_gms_internal_zzapy.beginArray();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    com_google_android_gms_internal_zzapy.beginArray();
                    zzb = this.bmC.zzb(com_google_android_gms_internal_zzapy);
                    if (map.put(zzb, this.bmD.zzb(com_google_android_gms_internal_zzapy)) != null) {
                        String valueOf = String.valueOf(zzb);
                        throw new zzaoq(new StringBuilder(String.valueOf(valueOf).length() + 15).append("duplicate key: ").append(valueOf).toString());
                    }
                    com_google_android_gms_internal_zzapy.endArray();
                }
                com_google_android_gms_internal_zzapy.endArray();
                return map;
            }
            com_google_android_gms_internal_zzapy.beginObject();
            while (com_google_android_gms_internal_zzapy.hasNext()) {
                zzapd.blQ.zzi(com_google_android_gms_internal_zzapy);
                zzb = this.bmC.zzb(com_google_android_gms_internal_zzapy);
                if (map.put(zzb, this.bmD.zzb(com_google_android_gms_internal_zzapy)) != null) {
                    valueOf = String.valueOf(zzb);
                    throw new zzaoq(new StringBuilder(String.valueOf(valueOf).length() + 15).append("duplicate key: ").append(valueOf).toString());
                }
            }
            com_google_android_gms_internal_zzapy.endObject();
            return map;
        }
    }

    public zzapq(zzapb com_google_android_gms_internal_zzapb, boolean z) {
        this.bkM = com_google_android_gms_internal_zzapb;
        this.bmB = z;
    }

    private zzaot<?> zza(zzaob com_google_android_gms_internal_zzaob, Type type) {
        return (type == Boolean.TYPE || type == Boolean.class) ? zzapw.bmX : com_google_android_gms_internal_zzaob.zza(zzapx.zzl(type));
    }

    public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Type bz = com_google_android_gms_internal_zzapx_T.bz();
        if (!Map.class.isAssignableFrom(com_google_android_gms_internal_zzapx_T.by())) {
            return null;
        }
        Type[] zzb = zzapa.zzb(bz, zzapa.zzf(bz));
        zzaot zza = zza(com_google_android_gms_internal_zzaob, zzb[0]);
        zzaot zza2 = com_google_android_gms_internal_zzaob.zza(zzapx.zzl(zzb[1]));
        zzapg zzb2 = this.bkM.zzb(com_google_android_gms_internal_zzapx_T);
        return new zza(this, com_google_android_gms_internal_zzaob, zzb[0], zza, zzb[1], zza2, zzb2);
    }
}
