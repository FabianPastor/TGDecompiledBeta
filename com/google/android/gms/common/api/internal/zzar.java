package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.internal.zzj;
import java.util.Map;

final class zzar extends zzay {
    final /* synthetic */ zzao zzfrl;
    private final Map<zze, zzaq> zzfrn;

    public zzar(zzao com_google_android_gms_common_api_internal_zzao, Map<zze, zzaq> map) {
        this.zzfrl = com_google_android_gms_common_api_internal_zzao;
        super(com_google_android_gms_common_api_internal_zzao);
        this.zzfrn = map;
    }

    public final void zzaib() {
        int i;
        int i2 = 1;
        int i3 = 0;
        int i4 = 1;
        int i5 = 0;
        for (zze com_google_android_gms_common_api_Api_zze : this.zzfrn.keySet()) {
            if (!com_google_android_gms_common_api_Api_zze.zzagg()) {
                i = 0;
                i4 = i5;
            } else if (!((zzaq) this.zzfrn.get(com_google_android_gms_common_api_Api_zze)).zzfpg) {
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
            i3 = this.zzfrl.zzfqc.isGooglePlayServicesAvailable(this.zzfrl.mContext);
        }
        if (i3 == 0 || (r0 == 0 && i4 == 0)) {
            if (this.zzfrl.zzfrf) {
                this.zzfrl.zzfrd.connect();
            }
            for (zze com_google_android_gms_common_api_Api_zze2 : this.zzfrn.keySet()) {
                zzj com_google_android_gms_common_internal_zzj = (zzj) this.zzfrn.get(com_google_android_gms_common_api_Api_zze2);
                if (!com_google_android_gms_common_api_Api_zze2.zzagg() || i3 == 0) {
                    com_google_android_gms_common_api_Api_zze2.zza(com_google_android_gms_common_internal_zzj);
                } else {
                    this.zzfrl.zzfqv.zza(new zzat(this, this.zzfrl, com_google_android_gms_common_internal_zzj));
                }
            }
            return;
        }
        this.zzfrl.zzfqv.zza(new zzas(this, this.zzfrl, new ConnectionResult(i3, null)));
    }
}
