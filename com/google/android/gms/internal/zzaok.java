package com.google.android.gms.internal;

import java.util.Map.Entry;
import java.util.Set;

public final class zzaok extends zzaoh {
    private final zzapf<String, zzaoh> ble = new zzapf();

    private zzaoh zzcm(Object obj) {
        return obj == null ? zzaoj.bld : new zzaon(obj);
    }

    public Set<Entry<String, zzaoh>> entrySet() {
        return this.ble.entrySet();
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof zzaok) && ((zzaok) obj).ble.equals(this.ble));
    }

    public boolean has(String str) {
        return this.ble.containsKey(str);
    }

    public int hashCode() {
        return this.ble.hashCode();
    }

    public void zza(String str, zzaoh com_google_android_gms_internal_zzaoh) {
        Object obj;
        if (com_google_android_gms_internal_zzaoh == null) {
            obj = zzaoj.bld;
        }
        this.ble.put(str, obj);
    }

    public void zzb(String str, Boolean bool) {
        zza(str, zzcm(bool));
    }

    public void zzcb(String str, String str2) {
        zza(str, zzcm(str2));
    }

    public zzaoh zzuo(String str) {
        return (zzaoh) this.ble.get(str);
    }

    public zzaoe zzup(String str) {
        return (zzaoe) this.ble.get(str);
    }
}
