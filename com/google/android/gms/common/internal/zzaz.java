package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.zzm;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzaz extends zzee implements zzay {
    public static zzay zzJ(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGoogleCertificatesApi");
        return queryLocalInterface instanceof zzay ? (zzay) queryLocalInterface : new zzba(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        IInterface zzrF;
        boolean zze;
        switch (i) {
            case 1:
                zzrF = zzrF();
                parcel2.writeNoException();
                zzef.zza(parcel2, zzrF);
                break;
            case 2:
                zzrF = zzrG();
                parcel2.writeNoException();
                zzef.zza(parcel2, zzrF);
                break;
            case 3:
                zze = zze(parcel.readString(), zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                zzef.zza(parcel2, zze);
                break;
            case 4:
                zze = zzf(parcel.readString(), zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                zzef.zza(parcel2, zze);
                break;
            case 5:
                zze = zza((zzm) zzef.zza(parcel, zzm.CREATOR), zza.zzM(parcel.readStrongBinder()));
                parcel2.writeNoException();
                zzef.zza(parcel2, zze);
                break;
            default:
                return false;
        }
        return true;
    }
}
