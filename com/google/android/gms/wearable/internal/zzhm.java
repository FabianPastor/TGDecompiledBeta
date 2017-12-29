package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.zzcl;
import com.google.android.gms.wearable.MessageApi.MessageListener;

final class zzhm implements zzcl<MessageListener> {
    private /* synthetic */ zzfe zzlhp;

    zzhm(zzfe com_google_android_gms_wearable_internal_zzfe) {
        this.zzlhp = com_google_android_gms_wearable_internal_zzfe;
    }

    public final void zzahz() {
    }

    public final /* synthetic */ void zzu(Object obj) {
        ((MessageListener) obj).onMessageReceived(this.zzlhp);
    }
}
