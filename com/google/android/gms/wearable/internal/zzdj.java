package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;

public abstract class zzdj extends zzee implements zzdi {
    public zzdj() {
        attachInterface(this, "com.google.android.gms.wearable.internal.IWearableCallbacks");
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (zza(i, parcel, parcel2, i2)) {
            return true;
        }
        switch (i) {
            case 2:
                zza((zzcu) zzef.zza(parcel, zzcu.CREATOR));
                break;
            case 3:
                zza((zzem) zzef.zza(parcel, zzem.CREATOR));
                break;
            case 4:
                zza((zzda) zzef.zza(parcel, zzda.CREATOR));
                break;
            case 5:
                zzT((DataHolder) zzef.zza(parcel, DataHolder.CREATOR));
                break;
            case 6:
                zza((zzce) zzef.zza(parcel, zzce.CREATOR));
                break;
            case 7:
                zza((zzes) zzef.zza(parcel, zzes.CREATOR));
                break;
            case 8:
                zza((zzdc) zzef.zza(parcel, zzdc.CREATOR));
                break;
            case 9:
                zza((zzde) zzef.zza(parcel, zzde.CREATOR));
                break;
            case 10:
                zza((zzcy) zzef.zza(parcel, zzcy.CREATOR));
                break;
            case 11:
                zza((Status) zzef.zza(parcel, Status.CREATOR));
                break;
            case 12:
                zza((zzew) zzef.zza(parcel, zzew.CREATOR));
                break;
            case 13:
                zza((zzcw) zzef.zza(parcel, zzcw.CREATOR));
                break;
            case 14:
                zza((zzei) zzef.zza(parcel, zzei.CREATOR));
                break;
            case 15:
                zza((zzbf) zzef.zza(parcel, zzbf.CREATOR));
                break;
            case 16:
                zzb((zzbf) zzef.zza(parcel, zzbf.CREATOR));
                break;
            case 17:
                zza((zzck) zzef.zza(parcel, zzck.CREATOR));
                break;
            case 18:
                zza((zzcm) zzef.zza(parcel, zzcm.CREATOR));
                break;
            case 19:
                zza((zzaz) zzef.zza(parcel, zzaz.CREATOR));
                break;
            case 20:
                zza((zzbb) zzef.zza(parcel, zzbb.CREATOR));
                break;
            case 22:
                zza((zzci) zzef.zza(parcel, zzci.CREATOR));
                break;
            case 23:
                zza((zzcg) zzef.zza(parcel, zzcg.CREATOR));
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                zza((zzf) zzef.zza(parcel, zzf.CREATOR));
                break;
            case 27:
                zza((zzeq) zzef.zza(parcel, zzeq.CREATOR));
                break;
            case 28:
                zza((zzcp) zzef.zza(parcel, zzcp.CREATOR));
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /*29*/:
                zza((zzct) zzef.zza(parcel, zzct.CREATOR));
                break;
            case NalUnitTypes.NAL_TYPE_RSV_VCL30 /*30*/:
                zza((zzcr) zzef.zza(parcel, zzcr.CREATOR));
                break;
            default:
                return false;
        }
        parcel2.writeNoException();
        return true;
    }
}
