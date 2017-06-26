package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import java.util.List;

public abstract class zzk extends zzee implements zzj {
    public static zzj zzad(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IIndoorBuildingDelegate");
        return queryLocalInterface instanceof zzj ? (zzj) queryLocalInterface : new zzl(iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        int activeLevelIndex;
        boolean isUnderground;
        switch (i) {
            case 1:
                activeLevelIndex = getActiveLevelIndex();
                parcel2.writeNoException();
                parcel2.writeInt(activeLevelIndex);
                break;
            case 2:
                activeLevelIndex = getDefaultLevelIndex();
                parcel2.writeNoException();
                parcel2.writeInt(activeLevelIndex);
                break;
            case 3:
                List levels = getLevels();
                parcel2.writeNoException();
                parcel2.writeBinderList(levels);
                break;
            case 4:
                isUnderground = isUnderground();
                parcel2.writeNoException();
                zzef.zza(parcel2, isUnderground);
                break;
            case 5:
                zzj com_google_android_gms_maps_model_internal_zzj;
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    com_google_android_gms_maps_model_internal_zzj = null;
                } else {
                    IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IIndoorBuildingDelegate");
                    com_google_android_gms_maps_model_internal_zzj = queryLocalInterface instanceof zzj ? (zzj) queryLocalInterface : new zzl(readStrongBinder);
                }
                isUnderground = zzb(com_google_android_gms_maps_model_internal_zzj);
                parcel2.writeNoException();
                zzef.zza(parcel2, isUnderground);
                break;
            case 6:
                activeLevelIndex = hashCodeRemote();
                parcel2.writeNoException();
                parcel2.writeInt(activeLevelIndex);
                break;
            default:
                return false;
        }
        return true;
    }
}
