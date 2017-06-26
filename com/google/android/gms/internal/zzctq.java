package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

public abstract class zzctq extends zzee implements zzctp {
    public zzctq() {
        attachInterface(this, "com.google.android.gms.signin.internal.ISignInCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 3:
                zzef.zza(parcel, ConnectionResult.CREATOR);
                zzef.zza(parcel, zzctm.CREATOR);
                break;
            case 4:
                zzef.zza(parcel, Status.CREATOR);
                break;
            case 6:
                zzef.zza(parcel, Status.CREATOR);
                break;
            case 7:
                zzef.zza(parcel, Status.CREATOR);
                zzef.zza(parcel, GoogleSignInAccount.CREATOR);
                break;
            case 8:
                zzb((zzctw) zzef.zza(parcel, zzctw.CREATOR));
                break;
            default:
                return false;
        }
        parcel2.writeNoException();
        return true;
    }
}
