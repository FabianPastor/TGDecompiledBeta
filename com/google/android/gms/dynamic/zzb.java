package com.google.android.gms.dynamic;

import java.util.Iterator;

final class zzb implements zzo<T> {
    private /* synthetic */ zza zzaSv;

    zzb(zza com_google_android_gms_dynamic_zza) {
        this.zzaSv = com_google_android_gms_dynamic_zza;
    }

    public final void zza(T t) {
        this.zzaSv.zzaSr = t;
        Iterator it = this.zzaSv.zzaSt.iterator();
        while (it.hasNext()) {
            ((zzi) it.next()).zzb(this.zzaSv.zzaSr);
        }
        this.zzaSv.zzaSt.clear();
        this.zzaSv.zzaSs = null;
    }
}
