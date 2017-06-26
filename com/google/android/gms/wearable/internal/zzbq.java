package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.internal.zzbdv;
import com.google.android.gms.wearable.DataApi.DataListener;

final class zzbq implements zzc<DataListener> {
    private /* synthetic */ IntentFilter[] zzbRV;

    zzbq(IntentFilter[] intentFilterArr) {
        this.zzbRV = intentFilterArr;
    }

    public final /* synthetic */ void zza(zzfw com_google_android_gms_wearable_internal_zzfw, zzbay com_google_android_gms_internal_zzbay, Object obj, zzbdv com_google_android_gms_internal_zzbdv) throws RemoteException {
        com_google_android_gms_wearable_internal_zzfw.zza(com_google_android_gms_internal_zzbay, (DataListener) obj, com_google_android_gms_internal_zzbdv, this.zzbRV);
    }
}
