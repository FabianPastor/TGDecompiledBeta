package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class fh extends zzed implements fg {
    fh(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
    }

    public final void zzDS() throws RemoteException {
        zzb(2, zzZ());
    }

    public final fk[] zza(IObjectWrapper iObjectWrapper, fc fcVar, fm fmVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (Parcelable) fcVar);
        zzef.zza(zzZ, (Parcelable) fmVar);
        Parcel zza = zza(3, zzZ);
        fk[] fkVarArr = (fk[]) zza.createTypedArray(fk.CREATOR);
        zza.recycle();
        return fkVarArr;
    }
}
