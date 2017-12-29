package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzd extends zzbfm {
    public static final Creator<zzd> CREATOR = new zze();
    private zzem zzlhx;
    private IntentFilter[] zzlhy;
    private String zzlhz;
    private String zzlia;

    zzd(IBinder iBinder, IntentFilter[] intentFilterArr, String str, String str2) {
        zzem com_google_android_gms_wearable_internal_zzem = null;
        if (iBinder != null) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableListener");
                com_google_android_gms_wearable_internal_zzem = queryLocalInterface instanceof zzem ? (zzem) queryLocalInterface : new zzeo(iBinder);
            }
            this.zzlhx = com_google_android_gms_wearable_internal_zzem;
        } else {
            this.zzlhx = null;
        }
        this.zzlhy = intentFilterArr;
        this.zzlhz = str;
        this.zzlia = str2;
    }

    public zzd(zzhk com_google_android_gms_wearable_internal_zzhk) {
        this.zzlhx = com_google_android_gms_wearable_internal_zzhk;
        this.zzlhy = com_google_android_gms_wearable_internal_zzhk.zzbkg();
        this.zzlhz = com_google_android_gms_wearable_internal_zzhk.zzbkh();
        this.zzlia = null;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlhx == null ? null : this.zzlhx.asBinder(), false);
        zzbfp.zza(parcel, 3, this.zzlhy, i, false);
        zzbfp.zza(parcel, 4, this.zzlhz, false);
        zzbfp.zza(parcel, 5, this.zzlia, false);
        zzbfp.zzai(parcel, zze);
    }
}
