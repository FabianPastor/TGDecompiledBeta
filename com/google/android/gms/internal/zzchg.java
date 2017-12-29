package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;

public final class zzchg extends zzeu implements zzche {
    zzchg(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.measurement.internal.IMeasurementService");
    }

    public final List<zzcln> zza(zzcgi com_google_android_gms_internal_zzcgi, boolean z) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzew.zza(zzbe, z);
        zzbe = zza(7, zzbe);
        List createTypedArrayList = zzbe.createTypedArrayList(zzcln.CREATOR);
        zzbe.recycle();
        return createTypedArrayList;
    }

    public final List<zzcgl> zza(String str, String str2, zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzbe = zza(16, zzbe);
        List createTypedArrayList = zzbe.createTypedArrayList(zzcgl.CREATOR);
        zzbe.recycle();
        return createTypedArrayList;
    }

    public final List<zzcln> zza(String str, String str2, String str3, boolean z) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzbe.writeString(str3);
        zzew.zza(zzbe, z);
        zzbe = zza(15, zzbe);
        List createTypedArrayList = zzbe.createTypedArrayList(zzcln.CREATOR);
        zzbe.recycle();
        return createTypedArrayList;
    }

    public final List<zzcln> zza(String str, String str2, boolean z, zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzew.zza(zzbe, z);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzbe = zza(14, zzbe);
        List createTypedArrayList = zzbe.createTypedArrayList(zzcln.CREATOR);
        zzbe.recycle();
        return createTypedArrayList;
    }

    public final void zza(long j, String str, String str2, String str3) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeLong(j);
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzbe.writeString(str3);
        zzb(10, zzbe);
    }

    public final void zza(zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzb(4, zzbe);
    }

    public final void zza(zzcgl com_google_android_gms_internal_zzcgl, zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgl);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzb(12, zzbe);
    }

    public final void zza(zzcha com_google_android_gms_internal_zzcha, zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcha);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzb(1, zzbe);
    }

    public final void zza(zzcha com_google_android_gms_internal_zzcha, String str, String str2) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcha);
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzb(5, zzbe);
    }

    public final void zza(zzcln com_google_android_gms_internal_zzcln, zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcln);
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzb(2, zzbe);
    }

    public final byte[] zza(zzcha com_google_android_gms_internal_zzcha, String str) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcha);
        zzbe.writeString(str);
        zzbe = zza(9, zzbe);
        byte[] createByteArray = zzbe.createByteArray();
        zzbe.recycle();
        return createByteArray;
    }

    public final void zzb(zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzb(6, zzbe);
    }

    public final void zzb(zzcgl com_google_android_gms_internal_zzcgl) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgl);
        zzb(13, zzbe);
    }

    public final String zzc(zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzbe = zza(11, zzbe);
        String readString = zzbe.readString();
        zzbe.recycle();
        return readString;
    }

    public final void zzd(zzcgi com_google_android_gms_internal_zzcgi) throws RemoteException {
        Parcel zzbe = zzbe();
        zzew.zza(zzbe, (Parcelable) com_google_android_gms_internal_zzcgi);
        zzb(18, zzbe);
    }

    public final List<zzcgl> zzj(String str, String str2, String str3) throws RemoteException {
        Parcel zzbe = zzbe();
        zzbe.writeString(str);
        zzbe.writeString(str2);
        zzbe.writeString(str3);
        zzbe = zza(17, zzbe);
        List createTypedArrayList = zzbe.createTypedArrayList(zzcgl.CREATOR);
        zzbe.recycle();
        return createTypedArrayList;
    }
}
