package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.Api.zze;
import java.util.ArrayList;

final class zzbci extends zzbcm {
    private /* synthetic */ zzbcc zzaDp;
    private final ArrayList<zze> zzaDv;

    public zzbci(zzbcc com_google_android_gms_internal_zzbcc, ArrayList<zze> arrayList) {
        this.zzaDp = com_google_android_gms_internal_zzbcc;
        super(com_google_android_gms_internal_zzbcc);
        this.zzaDv = arrayList;
    }

    @WorkerThread
    public final void zzpV() {
        this.zzaDp.zzaCZ.zzaCl.zzaDG = this.zzaDp.zzqb();
        ArrayList arrayList = this.zzaDv;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((zze) obj).zza(this.zzaDp.zzaDl, this.zzaDp.zzaCZ.zzaCl.zzaDG);
        }
    }
}
