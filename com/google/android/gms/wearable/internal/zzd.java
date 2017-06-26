package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzd extends zza {
    public static final Creator<zzd> CREATOR = new zze();
    private zzdk zzbRH;
    private IntentFilter[] zzbRI;
    private String zzbRJ;
    private String zzbRK;

    zzd(IBinder iBinder, IntentFilter[] intentFilterArr, String str, String str2) {
        zzdk com_google_android_gms_wearable_internal_zzdk = null;
        if (iBinder != null) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableListener");
                com_google_android_gms_wearable_internal_zzdk = queryLocalInterface instanceof zzdk ? (zzdk) queryLocalInterface : new zzdm(iBinder);
            }
            this.zzbRH = com_google_android_gms_wearable_internal_zzdk;
        } else {
            this.zzbRH = null;
        }
        this.zzbRI = intentFilterArr;
        this.zzbRJ = str;
        this.zzbRK = str2;
    }

    public zzd(zzga com_google_android_gms_wearable_internal_zzga) {
        this.zzbRH = com_google_android_gms_wearable_internal_zzga;
        this.zzbRI = com_google_android_gms_wearable_internal_zzga.zzDX();
        this.zzbRJ = com_google_android_gms_wearable_internal_zzga.zzDY();
        this.zzbRK = null;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = com.google.android.gms.common.internal.safeparcel.zzd.zze(parcel);
        com.google.android.gms.common.internal.safeparcel.zzd.zza(parcel, 2, this.zzbRH == null ? null : this.zzbRH.asBinder(), false);
        com.google.android.gms.common.internal.safeparcel.zzd.zza(parcel, 3, this.zzbRI, i, false);
        com.google.android.gms.common.internal.safeparcel.zzd.zza(parcel, 4, this.zzbRJ, false);
        com.google.android.gms.common.internal.safeparcel.zzd.zza(parcel, 5, this.zzbRK, false);
        com.google.android.gms.common.internal.safeparcel.zzd.zzI(parcel, zze);
    }
}
