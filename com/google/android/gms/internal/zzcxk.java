package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

public abstract class zzcxk extends zzev implements zzcxj {
    public zzcxk() {
        attachInterface(this, "com.google.android.gms.signin.internal.ISignInCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 3:
                zzew.zza(parcel, ConnectionResult.CREATOR);
                zzew.zza(parcel, zzcxg.CREATOR);
                break;
            case 4:
                zzew.zza(parcel, Status.CREATOR);
                break;
            case 6:
                zzew.zza(parcel, Status.CREATOR);
                break;
            case 7:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, GoogleSignInAccount.CREATOR);
                break;
            case 8:
                zzb((zzcxq) zzew.zza(parcel, zzcxq.CREATOR));
                break;
            default:
                return false;
        }
        parcel2.writeNoException();
        return true;
    }
}
