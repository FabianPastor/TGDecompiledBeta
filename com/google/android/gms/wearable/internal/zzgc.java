package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbdz;
import com.google.android.gms.wearable.MessageApi.MessageListener;

final class zzgc implements zzbdz<MessageListener> {
    private /* synthetic */ zzdx zzbRB;

    zzgc(zzdx com_google_android_gms_wearable_internal_zzdx) {
        this.zzbRB = com_google_android_gms_wearable_internal_zzdx;
    }

    public final void zzpT() {
    }

    public final /* synthetic */ void zzq(Object obj) {
        ((MessageListener) obj).onMessageReceived(this.zzbRB);
    }
}
