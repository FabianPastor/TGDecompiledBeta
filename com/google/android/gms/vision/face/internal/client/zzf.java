package com.google.android.gms.vision.face.internal.client;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.zzdjw;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;

public final class zzf extends zzeu implements zze {
    zzf(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
    }

    public final void zzbjt() throws RemoteException {
        zzb(3, zzbe());
    }

    public final FaceParcel[] zzc(IObjectWrapper iObjectWrapper, zzdjw com_google_android_gms_internal_zzdjw) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) iObjectWrapper);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzdjw);
        Parcel zza = zza(1, zzbe);
        FaceParcel[] faceParcelArr = (FaceParcel[]) zza.createTypedArray(FaceParcel.CREATOR);
        zza.recycle();
        return faceParcelArr;
    }
}
