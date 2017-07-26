package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Status;

final class zzbft extends zzbfn {
    private final zzbaz<Status> zzaIz;

    public zzbft(zzbaz<Status> com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status) {
        this.zzaIz = com_google_android_gms_internal_zzbaz_com_google_android_gms_common_api_Status;
    }

    public final void zzaC(int i) throws RemoteException {
        this.zzaIz.setResult(new Status(i));
    }
}
