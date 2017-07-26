package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import java.util.ArrayList;
import java.util.List;

final class zzfk extends zzfc<GetConnectedNodesResult> {
    public zzfk(zzbaz<GetConnectedNodesResult> com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_NodeApi_GetConnectedNodesResult) {
        super(com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_NodeApi_GetConnectedNodesResult);
    }

    public final void zza(zzcy com_google_android_gms_wearable_internal_zzcy) {
        List arrayList = new ArrayList();
        arrayList.addAll(com_google_android_gms_wearable_internal_zzcy.zzbSO);
        zzR(new zzee(zzev.zzaY(com_google_android_gms_wearable_internal_zzcy.statusCode), arrayList));
    }
}
