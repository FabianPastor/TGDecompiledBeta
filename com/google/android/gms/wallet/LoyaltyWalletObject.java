package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.LabelValueRow;
import com.google.android.gms.wallet.wobs.LoyaltyPoints;
import com.google.android.gms.wallet.wobs.TextModuleData;
import com.google.android.gms.wallet.wobs.TimeInterval;
import com.google.android.gms.wallet.wobs.UriData;
import com.google.android.gms.wallet.wobs.WalletObjectMessage;
import java.util.ArrayList;

public final class LoyaltyWalletObject extends zzbfm {
    public static final Creator<LoyaltyWalletObject> CREATOR = new zzv();
    int state;
    String zzghu;
    String zzlbi;
    String zzlbj;
    String zzlbk;
    String zzlbl;
    String zzlbm;
    String zzlbn;
    String zzlbo;
    String zzlbp;
    ArrayList<WalletObjectMessage> zzlbq;
    TimeInterval zzlbr;
    ArrayList<LatLng> zzlbs;
    String zzlbt;
    String zzlbu;
    ArrayList<LabelValueRow> zzlbv;
    boolean zzlbw;
    ArrayList<UriData> zzlbx;
    ArrayList<TextModuleData> zzlby;
    ArrayList<UriData> zzlbz;
    LoyaltyPoints zzlca;
    String zzwc;

    LoyaltyWalletObject() {
        this.zzlbq = new ArrayList();
        this.zzlbs = new ArrayList();
        this.zzlbv = new ArrayList();
        this.zzlbx = new ArrayList();
        this.zzlby = new ArrayList();
        this.zzlbz = new ArrayList();
    }

    LoyaltyWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, int i, ArrayList<WalletObjectMessage> arrayList, TimeInterval timeInterval, ArrayList<LatLng> arrayList2, String str11, String str12, ArrayList<LabelValueRow> arrayList3, boolean z, ArrayList<UriData> arrayList4, ArrayList<TextModuleData> arrayList5, ArrayList<UriData> arrayList6, LoyaltyPoints loyaltyPoints) {
        this.zzwc = str;
        this.zzlbi = str2;
        this.zzlbj = str3;
        this.zzlbk = str4;
        this.zzghu = str5;
        this.zzlbl = str6;
        this.zzlbm = str7;
        this.zzlbn = str8;
        this.zzlbo = str9;
        this.zzlbp = str10;
        this.state = i;
        this.zzlbq = arrayList;
        this.zzlbr = timeInterval;
        this.zzlbs = arrayList2;
        this.zzlbt = str11;
        this.zzlbu = str12;
        this.zzlbv = arrayList3;
        this.zzlbw = z;
        this.zzlbx = arrayList4;
        this.zzlby = arrayList5;
        this.zzlbz = arrayList6;
        this.zzlca = loyaltyPoints;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzwc, false);
        zzbfp.zza(parcel, 3, this.zzlbi, false);
        zzbfp.zza(parcel, 4, this.zzlbj, false);
        zzbfp.zza(parcel, 5, this.zzlbk, false);
        zzbfp.zza(parcel, 6, this.zzghu, false);
        zzbfp.zza(parcel, 7, this.zzlbl, false);
        zzbfp.zza(parcel, 8, this.zzlbm, false);
        zzbfp.zza(parcel, 9, this.zzlbn, false);
        zzbfp.zza(parcel, 10, this.zzlbo, false);
        zzbfp.zza(parcel, 11, this.zzlbp, false);
        zzbfp.zzc(parcel, 12, this.state);
        zzbfp.zzc(parcel, 13, this.zzlbq, false);
        zzbfp.zza(parcel, 14, this.zzlbr, i, false);
        zzbfp.zzc(parcel, 15, this.zzlbs, false);
        zzbfp.zza(parcel, 16, this.zzlbt, false);
        zzbfp.zza(parcel, 17, this.zzlbu, false);
        zzbfp.zzc(parcel, 18, this.zzlbv, false);
        zzbfp.zza(parcel, 19, this.zzlbw);
        zzbfp.zzc(parcel, 20, this.zzlbx, false);
        zzbfp.zzc(parcel, 21, this.zzlby, false);
        zzbfp.zzc(parcel, 22, this.zzlbz, false);
        zzbfp.zza(parcel, 23, this.zzlca, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
