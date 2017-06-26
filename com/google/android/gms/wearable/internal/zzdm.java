package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import java.util.List;

public final class zzdm extends zzed implements zzdk {
    zzdm(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wearable.internal.IWearableListener");
    }

    public final void onConnectedNodes(List<zzeg> list) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeTypedList(list);
        zzc(5, zzZ);
    }

    public final void zzS(DataHolder dataHolder) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) dataHolder);
        zzc(1, zzZ);
    }

    public final void zza(zzaa com_google_android_gms_wearable_internal_zzaa) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzaa);
        zzc(8, zzZ);
    }

    public final void zza(zzai com_google_android_gms_wearable_internal_zzai) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzai);
        zzc(7, zzZ);
    }

    public final void zza(zzdx com_google_android_gms_wearable_internal_zzdx) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzdx);
        zzc(2, zzZ);
    }

    public final void zza(zzeg com_google_android_gms_wearable_internal_zzeg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzeg);
        zzc(3, zzZ);
    }

    public final void zza(zzi com_google_android_gms_wearable_internal_zzi) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzi);
        zzc(9, zzZ);
    }

    public final void zza(zzl com_google_android_gms_wearable_internal_zzl) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzl);
        zzc(6, zzZ);
    }

    public final void zzb(zzeg com_google_android_gms_wearable_internal_zzeg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_wearable_internal_zzeg);
        zzc(4, zzZ);
    }
}
