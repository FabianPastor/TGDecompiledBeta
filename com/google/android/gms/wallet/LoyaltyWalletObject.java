package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.util.zzb;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.zzd;
import com.google.android.gms.wallet.wobs.zzf;
import com.google.android.gms.wallet.wobs.zzj;
import com.google.android.gms.wallet.wobs.zzl;
import com.google.android.gms.wallet.wobs.zzn;
import com.google.android.gms.wallet.wobs.zzp;
import java.util.ArrayList;

public final class LoyaltyWalletObject extends zza {
    public static final Creator<LoyaltyWalletObject> CREATOR = new zzm();
    int state;
    String zzaJT;
    String zzbQA;
    String zzbQB;
    String zzbQC;
    String zzbQD;
    String zzbQE;
    String zzbQF;
    String zzbQG;
    String zzbQH;
    ArrayList<zzp> zzbQI;
    zzl zzbQJ;
    ArrayList<LatLng> zzbQK;
    String zzbQL;
    String zzbQM;
    ArrayList<zzd> zzbQN;
    boolean zzbQO;
    ArrayList<zzn> zzbQP;
    ArrayList<zzj> zzbQQ;
    ArrayList<zzn> zzbQR;
    zzf zzbQS;
    String zzkl;

    LoyaltyWalletObject() {
        this.zzbQI = zzb.zzyY();
        this.zzbQK = zzb.zzyY();
        this.zzbQN = zzb.zzyY();
        this.zzbQP = zzb.zzyY();
        this.zzbQQ = zzb.zzyY();
        this.zzbQR = zzb.zzyY();
    }

    LoyaltyWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, int i, ArrayList<zzp> arrayList, zzl com_google_android_gms_wallet_wobs_zzl, ArrayList<LatLng> arrayList2, String str11, String str12, ArrayList<zzd> arrayList3, boolean z, ArrayList<zzn> arrayList4, ArrayList<zzj> arrayList5, ArrayList<zzn> arrayList6, zzf com_google_android_gms_wallet_wobs_zzf) {
        this.zzkl = str;
        this.zzbQA = str2;
        this.zzbQB = str3;
        this.zzbQC = str4;
        this.zzaJT = str5;
        this.zzbQD = str6;
        this.zzbQE = str7;
        this.zzbQF = str8;
        this.zzbQG = str9;
        this.zzbQH = str10;
        this.state = i;
        this.zzbQI = arrayList;
        this.zzbQJ = com_google_android_gms_wallet_wobs_zzl;
        this.zzbQK = arrayList2;
        this.zzbQL = str11;
        this.zzbQM = str12;
        this.zzbQN = arrayList3;
        this.zzbQO = z;
        this.zzbQP = arrayList4;
        this.zzbQQ = arrayList5;
        this.zzbQR = arrayList6;
        this.zzbQS = com_google_android_gms_wallet_wobs_zzf;
    }

    public String getAccountId() {
        return this.zzbQA;
    }

    public String getAccountName() {
        return this.zzaJT;
    }

    public String getBarcodeAlternateText() {
        return this.zzbQD;
    }

    public String getBarcodeType() {
        return this.zzbQE;
    }

    public String getBarcodeValue() {
        return this.zzbQF;
    }

    public String getId() {
        return this.zzkl;
    }

    public String getIssuerName() {
        return this.zzbQB;
    }

    public String getProgramName() {
        return this.zzbQC;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzm.zza(this, parcel, i);
    }
}
