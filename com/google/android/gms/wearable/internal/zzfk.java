package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import java.util.ArrayList;
import java.util.List;

final class zzfk extends zzfc<GetConnectedNodesResult> {
    public zzfk(zzbay<GetConnectedNodesResult> com_google_android_gms_internal_zzbay_com_google_android_gms_wearable_NodeApi_GetConnectedNodesResult) {
        super(com_google_android_gms_internal_zzbay_com_google_android_gms_wearable_NodeApi_GetConnectedNodesResult);
    }

    public final void zza(zzcy com_google_android_gms_wearable_internal_zzcy) {
        List arrayList = new ArrayList();
        arrayList.addAll(com_google_android_gms_wearable_internal_zzcy.zzbSM);
        zzR(new zzee(zzev.zzaY(com_google_android_gms_wearable_internal_zzcy.statusCode), arrayList));
    }
}
