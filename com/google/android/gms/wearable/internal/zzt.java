package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.internal.zzbdw;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;

final class zzt implements zzc<CapabilityListener> {
    private /* synthetic */ IntentFilter[] zzbRX;

    zzt(IntentFilter[] intentFilterArr) {
        this.zzbRX = intentFilterArr;
    }

    public final /* synthetic */ void zza(zzfw com_google_android_gms_wearable_internal_zzfw, zzbaz com_google_android_gms_internal_zzbaz, Object obj, zzbdw com_google_android_gms_internal_zzbdw) throws RemoteException {
        com_google_android_gms_wearable_internal_zzfw.zza(com_google_android_gms_internal_zzbaz, (CapabilityListener) obj, com_google_android_gms_internal_zzbdw, this.zzbRX);
    }
}
