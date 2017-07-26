package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.internal.zzj;
import java.util.Map;

final class zzbcg extends zzbcn {
    final /* synthetic */ zzbcd zzaDp;
    private final Map<zze, zzbcf> zzaDr;

    public zzbcg(zzbcd com_google_android_gms_internal_zzbcd, Map<zze, zzbcf> map) {
        this.zzaDp = com_google_android_gms_internal_zzbcd;
        super(com_google_android_gms_internal_zzbcd);
        this.zzaDr = map;
    }

    @WorkerThread
    public final void zzpV() {
        int i;
        int i2 = 1;
        int i3 = 0;
        int i4 = 1;
        int i5 = 0;
        for (zze com_google_android_gms_common_api_Api_zze : this.zzaDr.keySet()) {
            if (!com_google_android_gms_common_api_Api_zze.zzpe()) {
                i = 0;
                i4 = i5;
            } else if (!((zzbcf) this.zzaDr.get(com_google_android_gms_common_api_Api_zze)).zzaCj) {
                i = 1;
                break;
            } else {
                i = i4;
                i4 = 1;
            }
            i5 = i4;
            i4 = i;
        }
        i2 = i5;
        i = 0;
        if (i2 != 0) {
            i3 = this.zzaDp.zzaCF.isGooglePlayServicesAvailable(this.zzaDp.mContext);
        }
        if (i3 == 0 || (r0 == 0 && i4 == 0)) {
            if (this.zzaDp.zzaDj) {
                this.zzaDp.zzaDh.connect();
            }
            for (zze com_google_android_gms_common_api_Api_zze2 : this.zzaDr.keySet()) {
                zzj com_google_android_gms_common_internal_zzj = (zzj) this.zzaDr.get(com_google_android_gms_common_api_Api_zze2);
                if (!com_google_android_gms_common_api_Api_zze2.zzpe() || i3 == 0) {
                    com_google_android_gms_common_api_Api_zze2.zza(com_google_android_gms_common_internal_zzj);
                } else {
                    this.zzaDp.zzaCZ.zza(new zzbci(this, this.zzaDp, com_google_android_gms_common_internal_zzj));
                }
            }
            return;
        }
        this.zzaDp.zzaCZ.zza(new zzbch(this, this.zzaDp, new ConnectionResult(i3, null)));
    }
}
