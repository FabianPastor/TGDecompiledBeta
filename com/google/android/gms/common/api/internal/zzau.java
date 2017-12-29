package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api.zze;
import java.util.ArrayList;

final class zzau extends zzay {
    private /* synthetic */ zzao zzfrl;
    private final ArrayList<zze> zzfrr;

    public zzau(zzao com_google_android_gms_common_api_internal_zzao, ArrayList<zze> arrayList) {
        this.zzfrl = com_google_android_gms_common_api_internal_zzao;
        super(com_google_android_gms_common_api_internal_zzao);
        this.zzfrr = arrayList;
    }

    public final void zzaib() {
        this.zzfrl.zzfqv.zzfpi.zzfsc = this.zzfrl.zzaih();
        ArrayList arrayList = this.zzfrr;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((zze) obj).zza(this.zzfrl.zzfrh, this.zzfrl.zzfqv.zzfpi.zzfsc);
        }
    }
}
