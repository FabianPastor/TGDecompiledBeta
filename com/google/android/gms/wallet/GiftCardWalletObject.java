package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class GiftCardWalletObject extends zza {
    public static final Creator<GiftCardWalletObject> CREATOR = new zzi();
    private String pin;
    private CommonWalletObject zzbOB = CommonWalletObject.zzDT().zzDU();
    private String zzbOC;
    private String zzbOD;
    private long zzbOE;
    private String zzbOF;
    private long zzbOG;
    private String zzbOH;

    GiftCardWalletObject() {
    }

    GiftCardWalletObject(CommonWalletObject commonWalletObject, String str, String str2, String str3, long j, String str4, long j2, String str5) {
        this.zzbOB = commonWalletObject;
        this.zzbOC = str;
        this.pin = str2;
        this.zzbOE = j;
        this.zzbOF = str4;
        this.zzbOG = j2;
        this.zzbOH = str5;
        this.zzbOD = str3;
    }

    public final String getId() {
        return this.zzbOB.getId();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOB, i, false);
        zzd.zza(parcel, 3, this.zzbOC, false);
        zzd.zza(parcel, 4, this.pin, false);
        zzd.zza(parcel, 5, this.zzbOD, false);
        zzd.zza(parcel, 6, this.zzbOE);
        zzd.zza(parcel, 7, this.zzbOF, false);
        zzd.zza(parcel, 8, this.zzbOG);
        zzd.zza(parcel, 9, this.zzbOH, false);
        zzd.zzI(parcel, zze);
    }
}
