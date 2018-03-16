package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;

public final class zzeq extends zzeu implements zzep {
    zzeq(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.wearable.internal.IWearableService");
    }

    public final void zza(zzek com_google_android_gms_wearable_internal_zzek, zzd com_google_android_gms_wearable_internal_zzd) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzek);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_wearable_internal_zzd);
        zzb(16, zzbe);
    }

    public final void zza(zzek com_google_android_gms_wearable_internal_zzek, zzei com_google_android_gms_wearable_internal_zzei, String str) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzek);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzei);
        zzbe.writeString(str);
        zzb(34, zzbe);
    }

    public final void zza(zzek com_google_android_gms_wearable_internal_zzek, String str, int i) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzek);
        zzbe.writeString(str);
        zzbe.writeInt(i);
        zzb(42, zzbe);
    }

    public final void zza(zzek com_google_android_gms_wearable_internal_zzek, String str, String str2, byte[] bArr) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzek);
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzbe.writeByteArray(bArr);
        zzb(12, zzbe);
    }

    public final void zzb(zzek com_google_android_gms_wearable_internal_zzek, zzei com_google_android_gms_wearable_internal_zzei, String str) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzek);
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzei);
        zzbe.writeString(str);
        zzb(35, zzbe);
    }

    public final void zzc(zzek com_google_android_gms_wearable_internal_zzek, String str) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (IInterface) com_google_android_gms_wearable_internal_zzek);
        zzbe.writeString(str);
        zzb(32, zzbe);
    }
}
