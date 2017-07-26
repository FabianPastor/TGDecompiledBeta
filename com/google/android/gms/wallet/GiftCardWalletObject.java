package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class GiftCardWalletObject extends zza {
    public static final Creator<GiftCardWalletObject> CREATOR = new zzi();
    private String pin;
    private CommonWalletObject zzbOD = CommonWalletObject.zzDU().zzDV();
    private String zzbOE;
    private String zzbOF;
    private long zzbOG;
    private String zzbOH;
    private long zzbOI;
    private String zzbOJ;

    GiftCardWalletObject() {
    }

    GiftCardWalletObject(CommonWalletObject commonWalletObject, String str, String str2, String str3, long j, String str4, long j2, String str5) {
        this.zzbOD = commonWalletObject;
        this.zzbOE = str;
        this.pin = str2;
        this.zzbOG = j;
        this.zzbOH = str4;
        this.zzbOI = j2;
        this.zzbOJ = str5;
        this.zzbOF = str3;
    }

    public final String getId() {
        return this.zzbOD.getId();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOD, i, false);
        zzd.zza(parcel, 3, this.zzbOE, false);
        zzd.zza(parcel, 4, this.pin, false);
        zzd.zza(parcel, 5, this.zzbOF, false);
        zzd.zza(parcel, 6, this.zzbOG);
        zzd.zza(parcel, 7, this.zzbOH, false);
        zzd.zza(parcel, 8, this.zzbOI);
        zzd.zza(parcel, 9, this.zzbOJ, false);
        zzd.zzI(parcel, zze);
    }
}
