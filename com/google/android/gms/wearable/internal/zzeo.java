package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;
import java.util.List;

public final class zzeo extends zzeu implements zzem {
    zzeo(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wearable.internal.IWearableListener");
    }

    public final void onConnectedNodes(List<zzfo> list) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeTypedList(list);
        zzc(5, zzbe);
    }

    public final void zza(zzah com_google_android_gms_wearable_internal_zzah) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzah);
        zzc(8, zzbe);
    }

    public final void zza(zzaw com_google_android_gms_wearable_internal_zzaw) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzaw);
        zzc(7, zzbe);
    }

    public final void zza(zzfe com_google_android_gms_wearable_internal_zzfe) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzfe);
        zzc(2, zzbe);
    }

    public final void zza(zzfo com_google_android_gms_wearable_internal_zzfo) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzfo);
        zzc(3, zzbe);
    }

    public final void zza(zzi com_google_android_gms_wearable_internal_zzi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzi);
        zzc(9, zzbe);
    }

    public final void zza(zzl com_google_android_gms_wearable_internal_zzl) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzl);
        zzc(6, zzbe);
    }

    public final void zzas(DataHolder dataHolder) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) dataHolder);
        zzc(1, zzbe);
    }

    public final void zzb(zzfo com_google_android_gms_wearable_internal_zzfo) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzfo);
        zzc(4, zzbe);
    }
}
