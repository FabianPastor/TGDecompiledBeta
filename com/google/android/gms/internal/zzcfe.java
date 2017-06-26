package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;

public final class zzcfe extends zzed implements zzcfc {
    zzcfe(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.measurement.internal.IMeasurementService");
    }

    public final List<zzcjh> zza(zzceg com_google_android_gms_internal_zzceg, boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzef.zza(zzZ, z);
        zzZ = zza(7, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcjh.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final List<zzcej> zza(String str, String str2, zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzZ = zza(16, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcej.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final List<zzcjh> zza(String str, String str2, String str3, boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzZ.writeString(str3);
        zzef.zza(zzZ, z);
        zzZ = zza(15, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcjh.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final List<zzcjh> zza(String str, String str2, boolean z, zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzef.zza(zzZ, z);
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzZ = zza(14, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcjh.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }

    public final void zza(long j, String str, String str2, String str3) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeLong(j);
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzZ.writeString(str3);
        zzb(10, zzZ);
    }

    public final void zza(zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzb(4, zzZ);
    }

    public final void zza(zzcej com_google_android_gms_internal_zzcej, zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzcej);
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzb(12, zzZ);
    }

    public final void zza(zzcey com_google_android_gms_internal_zzcey, zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzcey);
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzb(1, zzZ);
    }

    public final void zza(zzcey com_google_android_gms_internal_zzcey, String str, String str2) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzcey);
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzb(5, zzZ);
    }

    public final void zza(zzcjh com_google_android_gms_internal_zzcjh, zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzcjh);
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzb(2, zzZ);
    }

    public final byte[] zza(zzcey com_google_android_gms_internal_zzcey, String str) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzcey);
        zzZ.writeString(str);
        zzZ = zza(9, zzZ);
        byte[] createByteArray = zzZ.createByteArray();
        zzZ.recycle();
        return createByteArray;
    }

    public final void zzb(zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzb(6, zzZ);
    }

    public final void zzb(zzcej com_google_android_gms_internal_zzcej) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzcej);
        zzb(13, zzZ);
    }

    public final String zzc(zzceg com_google_android_gms_internal_zzceg) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) com_google_android_gms_internal_zzceg);
        zzZ = zza(11, zzZ);
        String readString = zzZ.readString();
        zzZ.recycle();
        return readString;
    }

    public final List<zzcej> zzk(String str, String str2, String str3) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzZ.writeString(str2);
        zzZ.writeString(str3);
        zzZ = zza(17, zzZ);
        List createTypedArrayList = zzZ.createTypedArrayList(zzcej.CREATOR);
        zzZ.recycle();
        return createTypedArrayList;
    }
}
