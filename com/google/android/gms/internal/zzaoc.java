package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class zzaoc {
    private final List<zzaou> bkL = new ArrayList();
    private zzapc bkV = zzapc.blF;
    private zzaor bkW = zzaor.DEFAULT;
    private zzaoa bkX = zzanz.bkD;
    private final Map<Type, zzaod<?>> bkY = new HashMap();
    private final List<zzaou> bkZ = new ArrayList();
    private int bla = 2;
    private int blb = 2;
    private boolean blc = true;

    private void zza(String str, int i, int i2, List<zzaou> list) {
        Object com_google_android_gms_internal_zzanw;
        if (str != null && !"".equals(str.trim())) {
            com_google_android_gms_internal_zzanw = new zzanw(str);
        } else if (i != 2 && i2 != 2) {
            com_google_android_gms_internal_zzanw = new zzanw(i, i2);
        } else {
            return;
        }
        list.add(zzaos.zza(zzapx.zzr(Date.class), com_google_android_gms_internal_zzanw));
        list.add(zzaos.zza(zzapx.zzr(Timestamp.class), com_google_android_gms_internal_zzanw));
        list.add(zzaos.zza(zzapx.zzr(java.sql.Date.class), com_google_android_gms_internal_zzanw));
    }

    public zzaoc aO() {
        this.blc = false;
        return this;
    }

    public zzaob aP() {
        List arrayList = new ArrayList();
        arrayList.addAll(this.bkL);
        Collections.reverse(arrayList);
        arrayList.addAll(this.bkZ);
        zza(null, this.bla, this.blb, arrayList);
        return new zzaob(this.bkV, this.bkX, this.bkY, false, false, false, this.blc, false, false, this.bkW, arrayList);
    }

    public zzaoc zza(Type type, Object obj) {
        boolean z = (obj instanceof zzaop) || (obj instanceof zzaog) || (obj instanceof zzaod) || (obj instanceof zzaot);
        zzaoz.zzbs(z);
        if (obj instanceof zzaod) {
            this.bkY.put(type, (zzaod) obj);
        }
        if ((obj instanceof zzaop) || (obj instanceof zzaog)) {
            this.bkL.add(zzaos.zzb(zzapx.zzl(type), obj));
        }
        if (obj instanceof zzaot) {
            this.bkL.add(zzapw.zza(zzapx.zzl(type), (zzaot) obj));
        }
        return this;
    }

    public zzaoc zza(zzanx... com_google_android_gms_internal_zzanxArr) {
        for (zzanx zza : com_google_android_gms_internal_zzanxArr) {
            this.bkV = this.bkV.zza(zza, true, true);
        }
        return this;
    }

    public zzaoc zzf(int... iArr) {
        this.bkV = this.bkV.zzg(iArr);
        return this;
    }
}
