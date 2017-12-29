package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zzq;

public abstract class zza {
    private int zzeie;

    public zza(int i) {
        this.zzeie = i;
    }

    private static Status zza(RemoteException remoteException) {
        StringBuilder stringBuilder = new StringBuilder();
        if (zzq.zzamh() && (remoteException instanceof TransactionTooLargeException)) {
            stringBuilder.append("TransactionTooLargeException: ");
        }
        stringBuilder.append(remoteException.getLocalizedMessage());
        return new Status(8, stringBuilder.toString());
    }

    public abstract void zza(zzae com_google_android_gms_common_api_internal_zzae, boolean z);

    public abstract void zza(zzbo<?> com_google_android_gms_common_api_internal_zzbo_) throws DeadObjectException;

    public abstract void zzs(Status status);
}
