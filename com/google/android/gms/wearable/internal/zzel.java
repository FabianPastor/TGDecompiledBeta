package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzev;
import com.google.android.gms.internal.zzew;

public abstract class zzel extends zzev implements zzek {
    public zzel() {
        attachInterface(this, "com.google.android.gms.wearable.internal.IWearableCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 2:
                zza((zzdw) zzew.zza(parcel, zzdw.CREATOR));
                break;
            case 3:
                zza((zzfu) zzew.zza(parcel, zzfu.CREATOR));
                break;
            case 4:
                zza((zzec) zzew.zza(parcel, zzec.CREATOR));
                break;
            case 5:
                zzat((DataHolder) zzew.zza(parcel, DataHolder.CREATOR));
                break;
            case 6:
                zza((zzdg) zzew.zza(parcel, zzdg.CREATOR));
                break;
            case 7:
                zza((zzga) zzew.zza(parcel, zzga.CREATOR));
                break;
            case 8:
                zza((zzee) zzew.zza(parcel, zzee.CREATOR));
                break;
            case 9:
                zza((zzeg) zzew.zza(parcel, zzeg.CREATOR));
                break;
            case 10:
                zza((zzea) zzew.zza(parcel, zzea.CREATOR));
                break;
            case 11:
                zza((Status) zzew.zza(parcel, Status.CREATOR));
                break;
            case 12:
                zza((zzge) zzew.zza(parcel, zzge.CREATOR));
                break;
            case 13:
                zza((zzdy) zzew.zza(parcel, zzdy.CREATOR));
                break;
            case 14:
                zza((zzfq) zzew.zza(parcel, zzfq.CREATOR));
                break;
            case 15:
                zza((zzbt) zzew.zza(parcel, zzbt.CREATOR));
                break;
            case 16:
                zzb((zzbt) zzew.zza(parcel, zzbt.CREATOR));
                break;
            case 17:
                zza((zzdm) zzew.zza(parcel, zzdm.CREATOR));
                break;
            case 18:
                zza((zzdo) zzew.zza(parcel, zzdo.CREATOR));
                break;
            case 19:
                zza((zzbn) zzew.zza(parcel, zzbn.CREATOR));
                break;
            case 20:
                zza((zzbp) zzew.zza(parcel, zzbp.CREATOR));
                break;
            case 22:
                zza((zzdk) zzew.zza(parcel, zzdk.CREATOR));
                break;
            case 23:
                zza((zzdi) zzew.zza(parcel, zzdi.CREATOR));
                break;
            case 26:
                zza((zzf) zzew.zza(parcel, zzf.CREATOR));
                break;
            case 27:
                zza((zzfy) zzew.zza(parcel, zzfy.CREATOR));
                break;
            case 28:
                zza((zzdr) zzew.zza(parcel, zzdr.CREATOR));
                break;
            case 29:
                zza((zzdv) zzew.zza(parcel, zzdv.CREATOR));
                break;
            case 30:
                zza((zzdt) zzew.zza(parcel, zzdt.CREATOR));
                break;
            default:
                return false;
        }
        parcel2.writeNoException();
        return true;
    }
}
