package com.google.android.gms.maps.model.internal;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;

public abstract class zzb extends zzee implements zza {
    public static zza zzaa(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IBitmapDescriptorFactoryDelegate");
        return queryLocalInterface instanceof zza ? (zza) queryLocalInterface : new zzc(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        IInterface zzbo;
        switch (i) {
            case 1:
                zzbo = zzbo(parcel.readInt());
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            case 2:
                zzbo = zzdC(parcel.readString());
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            case 3:
                zzbo = zzdD(parcel.readString());
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            case 4:
                zzbo = zzwl();
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            case 5:
                zzbo = zze(parcel.readFloat());
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            case 6:
                zzbo = zzd((Bitmap) zzef.zza(parcel, Bitmap.CREATOR));
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            case 7:
                zzbo = zzdE(parcel.readString());
                parcel2.writeNoException();
                zzef.zza(parcel2, zzbo);
                break;
            default:
                return false;
        }
        return true;
    }
}
