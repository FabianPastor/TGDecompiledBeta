package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public abstract class zzchf extends zzev implements zzche {
    public zzchf() {
        attachInterface(this, "com.google.android.gms.measurement.internal.IMeasurementService");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        List zza;
        switch (i) {
            case 1:
                zza((zzcha) zzew.zza(parcel, zzcha.CREATOR), (zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                break;
            case 2:
                zza((zzcln) zzew.zza(parcel, zzcln.CREATOR), (zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                break;
            case 4:
                zza((zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                break;
            case 5:
                zza((zzcha) zzew.zza(parcel, zzcha.CREATOR), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                break;
            case 6:
                zzb((zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                break;
            case 7:
                zza = zza((zzcgi) zzew.zza(parcel, zzcgi.CREATOR), zzew.zza(parcel));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 9:
                byte[] zza2 = zza((zzcha) zzew.zza(parcel, zzcha.CREATOR), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeByteArray(zza2);
                break;
            case 10:
                zza(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                break;
            case 11:
                String zzc = zzc((zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                parcel2.writeString(zzc);
                break;
            case 12:
                zza((zzcgl) zzew.zza(parcel, zzcgl.CREATOR), (zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                break;
            case 13:
                zzb((zzcgl) zzew.zza(parcel, zzcgl.CREATOR));
                parcel2.writeNoException();
                break;
            case 14:
                zza = zza(parcel.readString(), parcel.readString(), zzew.zza(parcel), (zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 15:
                zza = zza(parcel.readString(), parcel.readString(), parcel.readString(), zzew.zza(parcel));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 16:
                zza = zza(parcel.readString(), parcel.readString(), (zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 17:
                zza = zzj(parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeTypedList(zza);
                break;
            case 18:
                zzd((zzcgi) zzew.zza(parcel, zzcgi.CREATOR));
                parcel2.writeNoException();
                break;
            default:
                return false;
        }
        return true;
    }
}
