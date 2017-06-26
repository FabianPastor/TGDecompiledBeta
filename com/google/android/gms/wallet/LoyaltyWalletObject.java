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
    private String zzbOT;
    private String zzbOU;
    private String zzbOV;
    private String zzbOW;
    private String zzbOX;
    private String zzbOY;
    private String zzbOZ;
    private String zzbPa;
    private ArrayList<zzq> zzbPb;
    private zzm zzbPc;
    private ArrayList<LatLng> zzbPd;
    private String zzbPe;
    private String zzbPf;
    private ArrayList<zze> zzbPg;
    private boolean zzbPh;
    private ArrayList<zzo> zzbPi;
    private ArrayList<zzk> zzbPj;
    private ArrayList<zzo> zzbPk;
    private zzg zzbPl;
    private String zzkx;

    LoyaltyWalletObject() {
        this.zzbPb = new ArrayList();
        this.zzbPd = new ArrayList();
        this.zzbPg = new ArrayList();
        this.zzbPi = new ArrayList();
        this.zzbPj = new ArrayList();
        this.zzbPk = new ArrayList();
    }

    LoyaltyWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, int i, ArrayList<zzq> arrayList, zzm com_google_android_gms_wallet_wobs_zzm, ArrayList<LatLng> arrayList2, String str11, String str12, ArrayList<zze> arrayList3, boolean z, ArrayList<zzo> arrayList4, ArrayList<zzk> arrayList5, ArrayList<zzo> arrayList6, zzg com_google_android_gms_wallet_wobs_zzg) {
        this.zzkx = str;
        this.zzbOT = str2;
        this.zzbOU = str3;
        this.zzbOV = str4;
        this.zzaLx = str5;
        this.zzbOW = str6;
        this.zzbOX = str7;
        this.zzbOY = str8;
        this.zzbOZ = str9;
        this.zzbPa = str10;
        this.state = i;
        this.zzbPb = arrayList;
        this.zzbPc = com_google_android_gms_wallet_wobs_zzm;
        this.zzbPd = arrayList2;
        this.zzbPe = str11;
        this.zzbPf = str12;
        this.zzbPg = arrayList3;
        this.zzbPh = z;
        this.zzbPi = arrayList4;
        this.zzbPj = arrayList5;
        this.zzbPk = arrayList6;
        this.zzbPl = com_google_android_gms_wallet_wobs_zzg;
    }

    public final String getAccountId() {
        return this.zzbOT;
    }

    public final String getAccountName() {
        return this.zzaLx;
    }

    public final String getBarcodeAlternateText() {
        return this.zzbOW;
    }

    public final String getBarcodeType() {
        return this.zzbOX;
    }

    public final String getBarcodeValue() {
        return this.zzbOY;
    }

    public final String getId() {
        return this.zzkx;
    }

    public final String getIssuerName() {
        return this.zzbOU;
    }

    public final String getProgramName() {
        return this.zzbOV;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzkx, false);
        zzd.zza(parcel, 3, this.zzbOT, false);
        zzd.zza(parcel, 4, this.zzbOU, false);
        zzd.zza(parcel, 5, this.zzbOV, false);
        zzd.zza(parcel, 6, this.zzaLx, false);
        zzd.zza(parcel, 7, this.zzbOW, false);
        zzd.zza(parcel, 8, this.zzbOX, false);
        zzd.zza(parcel, 9, this.zzbOY, false);
        zzd.zza(parcel, 10, this.zzbOZ, false);
        zzd.zza(parcel, 11, this.zzbPa, false);
        zzd.zzc(parcel, 12, this.state);
        zzd.zzc(parcel, 13, this.zzbPb, false);
        zzd.zza(parcel, 14, this.zzbPc, i, false);
        zzd.zzc(parcel, 15, this.zzbPd, false);
        zzd.zza(parcel, 16, this.zzbPe, false);
        zzd.zza(parcel, 17, this.zzbPf, false);
        zzd.zzc(parcel, 18, this.zzbPg, false);
        zzd.zza(parcel, 19, this.zzbPh);
        zzd.zzc(parcel, 20, this.zzbPi, false);
        zzd.zzc(parcel, 21, this.zzbPj, false);
        zzd.zzc(parcel, 22, this.zzbPk, false);
        zzd.zza(parcel, 23, this.zzbPl, i, false);
        zzd.zzI(parcel, zze);
    }
}
