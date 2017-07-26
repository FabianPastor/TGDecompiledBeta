package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.internal.zzbdw;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

final class zzar implements zzc<ChannelListener> {
    private /* synthetic */ String zzakq;
    private /* synthetic */ IntentFilter[] zzbRX;

    zzar(String str, IntentFilter[] intentFilterArr) {
        this.zzakq = str;
        this.zzbRX = intentFilterArr;
    }

    public final /* synthetic */ void zza(zzfw com_google_android_gms_wearable_internal_zzfw, zzbaz com_google_android_gms_internal_zzbaz, Object obj, zzbdw com_google_android_gms_internal_zzbdw) throws RemoteException {
        com_google_android_gms_wearable_internal_zzfw.zza(com_google_android_gms_internal_zzbaz, (ChannelListener) obj, com_google_android_gms_internal_zzbdw, this.zzakq, this.zzbRX);
    }
}
