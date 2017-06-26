package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Status;

final class zzbfs extends zzbfm {
    private final zzbay<Status> zzaIz;

    public zzbfs(zzbay<Status> com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_Status) {
        this.zzaIz = com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_Status;
    }

    public final void zzaC(int i) throws RemoteException {
        this.zzaIz.setResult(new Status(i));
    }
}
