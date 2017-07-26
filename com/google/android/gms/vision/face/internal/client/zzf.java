package com.google.android.gms.vision.face.internal.client;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.fc;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;

public final class zzf extends zzed implements zze {
    zzf(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
    }

    public final void zzDP() throws RemoteException {
        zzb(3, zzZ());
    }

    public final boolean zzbN(int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzZ = zza(2, zzZ);
        boolean zza = zzef.zza(zzZ);
        zzZ.recycle();
        return zza;
    }

    public final FaceParcel[] zzc(IObjectWrapper iObjectWrapper, fc fcVar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (Parcelable) fcVar);
        Parcel zza = zza(1, zzZ);
        FaceParcel[] faceParcelArr = (FaceParcel[]) zza.createTypedArray(FaceParcel.CREATOR);
        zza.recycle();
        return faceParcelArr;
    }
}
