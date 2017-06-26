package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbdy;
import com.google.android.gms.wearable.NodeApi.NodeListener;

final class zzge implements zzbdy<NodeListener> {
    private /* synthetic */ zzeg zzbRA;

    zzge(zzeg com_google_android_gms_wearable_internal_zzeg) {
        this.zzbRA = com_google_android_gms_wearable_internal_zzeg;
    }

    public final void zzpT() {
    }

    public final /* synthetic */ void zzq(Object obj) {
        ((NodeListener) obj).onPeerDisconnected(this.zzbRA);
    }
}
