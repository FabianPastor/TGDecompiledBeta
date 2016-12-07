package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class zzaot {
    private final List<zzapl> boc = new ArrayList();
    private zzapt bom = zzapt.boW;
    private zzapi bon = zzapi.DEFAULT;
    private zzaor boo = zzaoq.IDENTITY;
    private final Map<Type, zzaou<?>> bop = new HashMap();
    private final List<zzapl> boq = new ArrayList();
    private int bor = 2;
    private int bos = 2;
    private boolean bot = true;

    private void zza(String str, int i, int i2, List<zzapl> list) {
        Object com_google_android_gms_internal_zzaon;
        if (str != null && !"".equals(str.trim())) {
            com_google_android_gms_internal_zzaon = new zzaon(str);
        } else if (i != 2 && i2 != 2) {
            com_google_android_gms_internal_zzaon = new zzaon(i, i2);
        } else {
            return;
        }
        list.add(zzapj.zza(zzaqo.zzr(Date.class), com_google_android_gms_internal_zzaon));
        list.add(zzapj.zza(zzaqo.zzr(Timestamp.class), com_google_android_gms_internal_zzaon));
        list.add(zzapj.zza(zzaqo.zzr(java.sql.Date.class), com_google_android_gms_internal_zzaon));
    }

    public zzaot aR() {
        this.bot = false;
        return this;
    }

    public zzaos aS() {
        List arrayList = new ArrayList();
        arrayList.addAll(this.boc);
        Collections.reverse(arrayList);
        arrayList.addAll(this.boq);
        zza(null, this.bor, this.bos, arrayList);
        return new zzaos(this.bom, this.boo, this.bop, false, false, false, this.bot, false, false, this.bon, arrayList);
    }

    public zzaot zza(Type type, Object obj) {
        boolean z = (obj instanceof zzapg) || (obj instanceof zzaox) || (obj instanceof zzaou) || (obj instanceof zzapk);
        zzapq.zzbt(z);
        if (obj instanceof zzaou) {
            this.bop.put(type, (zzaou) obj);
        }
        if ((obj instanceof zzapg) || (obj instanceof zzaox)) {
            this.boc.add(zzapj.zzb(zzaqo.zzl(type), obj));
        }
        if (obj instanceof zzapk) {
            this.boc.add(zzaqn.zza(zzaqo.zzl(type), (zzapk) obj));
        }
        return this;
    }

    public zzaot zza(zzaoo... com_google_android_gms_internal_zzaooArr) {
        for (zzaoo zza : com_google_android_gms_internal_zzaooArr) {
            this.bom = this.bom.zza(zza, true, true);
        }
        return this;
    }

    public zzaot zzf(int... iArr) {
        this.bom = this.bom.zzg(iArr);
        return this;
    }
}
