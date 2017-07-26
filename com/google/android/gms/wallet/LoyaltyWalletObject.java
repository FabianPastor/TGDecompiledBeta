package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.zze;
import com.google.android.gms.wallet.wobs.zzg;
import com.google.android.gms.wallet.wobs.zzk;
import com.google.android.gms.wallet.wobs.zzm;
import com.google.android.gms.wallet.wobs.zzo;
import com.google.android.gms.wallet.wobs.zzq;
import java.util.ArrayList;

public final class LoyaltyWalletObject extends zza {
    public static final Creator<LoyaltyWalletObject> CREATOR = new zzo();
    private int state;
    private String zzaLx;
    private String zzbOV;
    private String zzbOW;
    private String zzbOX;
    private String zzbOY;
    private String zzbOZ;
    private String zzbPa;
    private String zzbPb;
    private String zzbPc;
    private ArrayList<zzq> zzbPd;
    private zzm zzbPe;
    private ArrayList<LatLng> zzbPf;
    private String zzbPg;
    private String zzbPh;
    private ArrayList<zze> zzbPi;
    private boolean zzbPj;
    private ArrayList<zzo> zzbPk;
    private ArrayList<zzk> zzbPl;
    private ArrayList<zzo> zzbPm;
    private zzg zzbPn;
    private String zzkv;

    LoyaltyWalletObject() {
        this.zzbPd = new ArrayList();
        this.zzbPf = new ArrayList();
        this.zzbPi = new ArrayList();
        this.zzbPk = new ArrayList();
        this.zzbPl = new ArrayList();
        this.zzbPm = new ArrayList();
    }

    LoyaltyWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, int i, ArrayList<zzq> arrayList, zzm com_google_android_gms_wallet_wobs_zzm, ArrayList<LatLng> arrayList2, String str11, String str12, ArrayList<zze> arrayList3, boolean z, ArrayList<zzo> arrayList4, ArrayList<zzk> arrayList5, ArrayList<zzo> arrayList6, zzg com_google_android_gms_wallet_wobs_zzg) {
        this.zzkv = str;
        this.zzbOV = str2;
        this.zzbOW = str3;
        this.zzbOX = str4;
        this.zzaLx = str5;
        this.zzbOY = str6;
        this.zzbOZ = str7;
        this.zzbPa = str8;
        this.zzbPb = str9;
        this.zzbPc = str10;
        this.state = i;
        this.zzbPd = arrayList;
        this.zzbPe = com_google_android_gms_wallet_wobs_zzm;
        this.zzbPf = arrayList2;
        this.zzbPg = str11;
        this.zzbPh = str12;
        this.zzbPi = arrayList3;
        this.zzbPj = z;
        this.zzbPk = arrayList4;
        this.zzbPl = arrayList5;
        this.zzbPm = arrayList6;
        this.zzbPn = com_google_android_gms_wallet_wobs_zzg;
    }

    public final String getAccountId() {
        return this.zzbOV;
    }

    public final String getAccountName() {
        return this.zzaLx;
    }

    public final String getBarcodeAlternateText() {
        return this.zzbOY;
    }

    public final String getBarcodeType() {
        return this.zzbOZ;
    }

    public final String getBarcodeValue() {
        return this.zzbPa;
    }

    public final String getId() {
        return this.zzkv;
    }

    public final String getIssuerName() {
        return this.zzbOW;
    }

    public final String getProgramName() {
        return this.zzbOX;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzkv, false);
        zzd.zza(parcel, 3, this.zzbOV, false);
        zzd.zza(parcel, 4, this.zzbOW, false);
        zzd.zza(parcel, 5, this.zzbOX, false);
        zzd.zza(parcel, 6, this.zzaLx, false);
        zzd.zza(parcel, 7, this.zzbOY, false);
        zzd.zza(parcel, 8, this.zzbOZ, false);
        zzd.zza(parcel, 9, this.zzbPa, false);
        zzd.zza(parcel, 10, this.zzbPb, false);
        zzd.zza(parcel, 11, this.zzbPc, false);
        zzd.zzc(parcel, 12, this.state);
        zzd.zzc(parcel, 13, this.zzbPd, false);
        zzd.zza(parcel, 14, this.zzbPe, i, false);
        zzd.zzc(parcel, 15, this.zzbPf, false);
        zzd.zza(parcel, 16, this.zzbPg, false);
        zzd.zza(parcel, 17, this.zzbPh, false);
        zzd.zzc(parcel, 18, this.zzbPi, false);
        zzd.zza(parcel, 19, this.zzbPj);
        zzd.zzc(parcel, 20, this.zzbPk, false);
        zzd.zzc(parcel, 21, this.zzbPl, false);
        zzd.zzc(parcel, 22, this.zzbPm, false);
        zzd.zza(parcel, 23, this.zzbPn, i, false);
        zzd.zzI(parcel, zze);
    }
}
