package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

@KeepName
public class CommonWalletObject extends com.google.android.gms.common.internal.safeparcel.zza {
    public static final Creator<CommonWalletObject> CREATOR = new zzb();
    private String name;
    private int state;
    private String zzbOW;
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
    String zzkv;

    public final class zza {
        private /* synthetic */ CommonWalletObject zzbQG;

        private zza(CommonWalletObject commonWalletObject) {
            this.zzbQG = commonWalletObject;
        }

        public final CommonWalletObject zzDV() {
            return this.zzbQG;
        }

        public final zza zzgi(String str) {
            this.zzbQG.zzkv = str;
            return this;
        }
    }

    CommonWalletObject() {
        this.zzbPd = new ArrayList();
        this.zzbPf = new ArrayList();
        this.zzbPi = new ArrayList();
        this.zzbPk = new ArrayList();
        this.zzbPl = new ArrayList();
        this.zzbPm = new ArrayList();
    }

    CommonWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, ArrayList<zzq> arrayList, zzm com_google_android_gms_wallet_wobs_zzm, ArrayList<LatLng> arrayList2, String str9, String str10, ArrayList<zze> arrayList3, boolean z, ArrayList<zzo> arrayList4, ArrayList<zzk> arrayList5, ArrayList<zzo> arrayList6) {
        this.zzkv = str;
        this.zzbPc = str2;
        this.name = str3;
        this.zzbOW = str4;
        this.zzbOY = str5;
        this.zzbOZ = str6;
        this.zzbPa = str7;
        this.zzbPb = str8;
        this.state = i;
        this.zzbPd = arrayList;
        this.zzbPe = com_google_android_gms_wallet_wobs_zzm;
        this.zzbPf = arrayList2;
        this.zzbPg = str9;
        this.zzbPh = str10;
        this.zzbPi = arrayList3;
        this.zzbPj = z;
        this.zzbPk = arrayList4;
        this.zzbPl = arrayList5;
        this.zzbPm = arrayList6;
    }

    public static zza zzDU() {
        CommonWalletObject commonWalletObject = new CommonWalletObject();
        commonWalletObject.getClass();
        return new zza();
    }

    public final String getId() {
        return this.zzkv;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzkv, false);
        zzd.zza(parcel, 3, this.zzbPc, false);
        zzd.zza(parcel, 4, this.name, false);
        zzd.zza(parcel, 5, this.zzbOW, false);
        zzd.zza(parcel, 6, this.zzbOY, false);
        zzd.zza(parcel, 7, this.zzbOZ, false);
        zzd.zza(parcel, 8, this.zzbPa, false);
        zzd.zza(parcel, 9, this.zzbPb, false);
        zzd.zzc(parcel, 10, this.state);
        zzd.zzc(parcel, 11, this.zzbPd, false);
        zzd.zza(parcel, 12, this.zzbPe, i, false);
        zzd.zzc(parcel, 13, this.zzbPf, false);
        zzd.zza(parcel, 14, this.zzbPg, false);
        zzd.zza(parcel, 15, this.zzbPh, false);
        zzd.zzc(parcel, 16, this.zzbPi, false);
        zzd.zza(parcel, 17, this.zzbPj);
        zzd.zzc(parcel, 18, this.zzbPk, false);
        zzd.zzc(parcel, 19, this.zzbPl, false);
        zzd.zzc(parcel, 20, this.zzbPm, false);
        zzd.zzI(parcel, zze);
    }
}
