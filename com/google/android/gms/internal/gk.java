package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.zzab;

public abstract class gk extends zzee implements gj {
    public gk() {
        attachInterface(this, "com.google.android.gms.wallet.internal.IWalletServiceCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 1:
                zza(parcel.readInt(), (MaskedWallet) zzef.zza(parcel, MaskedWallet.CREATOR), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
                break;
            case 2:
                zza(parcel.readInt(), (FullWallet) zzef.zza(parcel, FullWallet.CREATOR), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
                break;
            case 3:
                zza(parcel.readInt(), zzef.zza(parcel), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
                break;
            case 4:
                zzg(parcel.readInt(), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
                break;
            case 6:
                zzb(parcel.readInt(), zzef.zza(parcel), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
                break;
            case 7:
                zzef.zza(parcel, Status.CREATOR);
                zzef.zza(parcel, fw.CREATOR);
                zzef.zza(parcel, Bundle.CREATOR);
                break;
            case 8:
                zzef.zza(parcel, Status.CREATOR);
                zzef.zza(parcel, Bundle.CREATOR);
                break;
            case 9:
                zza((Status) zzef.zza(parcel, Status.CREATOR), zzef.zza(parcel), (Bundle) zzef.zza(parcel, Bundle.CREATOR));
                break;
            case 10:
                zzef.zza(parcel, Status.CREATOR);
                zzef.zza(parcel, fy.CREATOR);
                zzef.zza(parcel, Bundle.CREATOR);
                break;
            case 11:
                zzef.zza(parcel, Status.CREATOR);
                zzef.zza(parcel, Bundle.CREATOR);
                break;
            case 12:
                zzef.zza(parcel, Status.CREATOR);
                zzef.zza(parcel, zzab.CREATOR);
                zzef.zza(parcel, Bundle.CREATOR);
                break;
            default:
                return false;
        }
        return true;
    }
}
