package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbdy;
import com.google.android.gms.wearable.MessageApi.MessageListener;

final class zzgc implements zzbdy<MessageListener> {
    private /* synthetic */ zzdx zzbRz;

    zzgc(zzdx com_google_android_gms_wearable_internal_zzdx) {
        this.zzbRz = com_google_android_gms_wearable_internal_zzdx;
    }

    public final void zzpT() {
    }

    public final /* synthetic */ void zzq(Object obj) {
        ((MessageListener) obj).onMessageReceived(this.zzbRz);
    }
}
