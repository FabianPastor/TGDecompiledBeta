package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public abstract class zzcfd extends zzee implements zzcfc {
    public zzcfd() {
        attachInterface(this, "com.google.android.gms.measurement.internal.IMeasurementService");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        List zza;
        switch (i) {
            case 1:
                zza((zzcey) zzef.zza(parcel, zzcey.CREATOR), (zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                break;
            case 2:
                zza((zzcjh) zzef.zza(parcel, zzcjh.CREATOR), (zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                break;
            case 4:
                zza((zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                break;
            case 5:
                zza((zzcey) zzef.zza(parcel, zzcey.CREATOR), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                break;
            case 6:
                zzb((zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                break;
            case 7:
                zza = zza((zzceg) zzef.zza(parcel, zzceg.CREATOR), zzef.zza(parcel));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 9:
                byte[] zza2 = zza((zzcey) zzef.zza(parcel, zzcey.CREATOR), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeByteArray(zza2);
                break;
            case 10:
                zza(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                break;
            case 11:
                String zzc = zzc((zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                parcel2.writeString(zzc);
                break;
            case 12:
                zza((zzcej) zzef.zza(parcel, zzcej.CREATOR), (zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                break;
            case 13:
                zzb((zzcej) zzef.zza(parcel, zzcej.CREATOR));
                parcel2.writeNoException();
                break;
            case 14:
                zza = zza(parcel.readString(), parcel.readString(), zzef.zza(parcel), (zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 15:
                zza = zza(parcel.readString(), parcel.readString(), parcel.readString(), zzef.zza(parcel));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 16:
                zza = zza(parcel.readString(), parcel.readString(), (zzceg) zzef.zza(parcel, zzceg.CREATOR));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 17:
                zza = zzk(parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            default:
                return false;
        }
        return true;
    }
}
