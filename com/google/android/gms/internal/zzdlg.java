package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.zzar;

public abstract class zzdlg extends zzev implements zzdlf {
    public zzdlg() {
        attachInterface(this, "com.google.android.gms.wallet.internal.IWalletServiceCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 1:
                zza(parcel.readInt(), (MaskedWallet) zzew.zza(parcel, MaskedWallet.CREATOR), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
                break;
            case 2:
                zza(parcel.readInt(), (FullWallet) zzew.zza(parcel, FullWallet.CREATOR), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
                break;
            case 3:
                zza(parcel.readInt(), zzew.zza(parcel), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
                break;
            case 4:
                zzg(parcel.readInt(), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
                break;
            case 6:
                parcel.readInt();
                zzew.zza(parcel);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 7:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, zzdkq.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 8:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 9:
                zza((Status) zzew.zza(parcel, Status.CREATOR), zzew.zza(parcel), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
                break;
            case 10:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, zzdks.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 11:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 12:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, zzar.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 13:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            case 14:
                zza((Status) zzew.zza(parcel, Status.CREATOR), (PaymentData) zzew.zza(parcel, PaymentData.CREATOR), (Bundle) zzew.zza(parcel, Bundle.CREATOR));
                break;
            case 15:
                zzew.zza(parcel, Status.CREATOR);
                zzew.zza(parcel, zzdku.CREATOR);
                zzew.zza(parcel, Bundle.CREATOR);
                break;
            default:
                return false;
        }
        return true;
    }
}
