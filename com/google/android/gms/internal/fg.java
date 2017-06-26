package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class fg extends zzed implements ff {
    fg(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
    }

    public final void zzDR() throws RemoteException {
        zzb(2, zzZ());
    }

    public final fj[] zza(IObjectWrapper iObjectWrapper, fb fbVar, fl flVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (Parcelable) fbVar);
        zzef.zza(zzZ, (Parcelable) flVar);
        Parcel zza = zza(3, zzZ);
        fj[] fjVarArr = (fj[]) zza.createTypedArray(fj.CREATOR);
        zza.recycle();
        return fjVarArr;
    }
}
