package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbdy;
import com.google.android.gms.wearable.NodeApi.NodeListener;

final class zzgd implements zzbdy<NodeListener> {
    private /* synthetic */ zzeg zzbRA;

    zzgd(zzeg com_google_android_gms_wearable_internal_zzeg) {
        this.zzbRA = com_google_android_gms_wearable_internal_zzeg;
    }

    public final void zzpT() {
    }

    public final /* synthetic */ void zzq(Object obj) {
        ((NodeListener) obj).onPeerConnected(this.zzbRA);
    }
}
