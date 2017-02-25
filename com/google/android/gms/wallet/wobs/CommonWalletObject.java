package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.util.zzb;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

@KeepName
public class CommonWalletObject extends com.google.android.gms.common.internal.safeparcel.zza {
    public static final Creator<CommonWalletObject> CREATOR = new zza();
    String name;
    int state;
    String zzbQB;
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
    String zzkl;

    public final class zza {
        final /* synthetic */ CommonWalletObject zzbSy;

        private zza(CommonWalletObject commonWalletObject) {
            this.zzbSy = commonWalletObject;
        }

        public CommonWalletObject zzUc() {
            return this.zzbSy;
        }

        public zza zzim(String str) {
            this.zzbSy.zzkl = str;
            return this;
        }
    }

    CommonWalletObject() {
        this.zzbQI = zzb.zzyY();
        this.zzbQK = zzb.zzyY();
        this.zzbQN = zzb.zzyY();
        this.zzbQP = zzb.zzyY();
        this.zzbQQ = zzb.zzyY();
        this.zzbQR = zzb.zzyY();
    }

    CommonWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, ArrayList<zzp> arrayList, zzl com_google_android_gms_wallet_wobs_zzl, ArrayList<LatLng> arrayList2, String str9, String str10, ArrayList<zzd> arrayList3, boolean z, ArrayList<zzn> arrayList4, ArrayList<zzj> arrayList5, ArrayList<zzn> arrayList6) {
        this.zzkl = str;
        this.zzbQH = str2;
        this.name = str3;
        this.zzbQB = str4;
        this.zzbQD = str5;
        this.zzbQE = str6;
        this.zzbQF = str7;
        this.zzbQG = str8;
        this.state = i;
        this.zzbQI = arrayList;
        this.zzbQJ = com_google_android_gms_wallet_wobs_zzl;
        this.zzbQK = arrayList2;
        this.zzbQL = str9;
        this.zzbQM = str10;
        this.zzbQN = arrayList3;
        this.zzbQO = z;
        this.zzbQP = arrayList4;
        this.zzbQQ = arrayList5;
        this.zzbQR = arrayList6;
    }

    public static zza zzUb() {
        CommonWalletObject commonWalletObject = new CommonWalletObject();
        commonWalletObject.getClass();
        return new zza();
    }

    public String getId() {
        return this.zzkl;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
