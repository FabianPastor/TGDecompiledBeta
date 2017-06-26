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
    private String zzbOU;
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
    String zzkx;

    public final class zza {
        private /* synthetic */ CommonWalletObject zzbQE;

        private zza(CommonWalletObject commonWalletObject) {
            this.zzbQE = commonWalletObject;
        }

        public final CommonWalletObject zzDU() {
            return this.zzbQE;
        }

        public final zza zzgi(String str) {
            this.zzbQE.zzkx = str;
            return this;
        }
    }

    CommonWalletObject() {
        this.zzbPb = new ArrayList();
        this.zzbPd = new ArrayList();
        this.zzbPg = new ArrayList();
        this.zzbPi = new ArrayList();
        this.zzbPj = new ArrayList();
        this.zzbPk = new ArrayList();
    }

    CommonWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, ArrayList<zzq> arrayList, zzm com_google_android_gms_wallet_wobs_zzm, ArrayList<LatLng> arrayList2, String str9, String str10, ArrayList<zze> arrayList3, boolean z, ArrayList<zzo> arrayList4, ArrayList<zzk> arrayList5, ArrayList<zzo> arrayList6) {
        this.zzkx = str;
        this.zzbPa = str2;
        this.name = str3;
        this.zzbOU = str4;
        this.zzbOW = str5;
        this.zzbOX = str6;
        this.zzbOY = str7;
        this.zzbOZ = str8;
        this.state = i;
        this.zzbPb = arrayList;
        this.zzbPc = com_google_android_gms_wallet_wobs_zzm;
        this.zzbPd = arrayList2;
        this.zzbPe = str9;
        this.zzbPf = str10;
        this.zzbPg = arrayList3;
        this.zzbPh = z;
        this.zzbPi = arrayList4;
        this.zzbPj = arrayList5;
        this.zzbPk = arrayList6;
    }

    public static zza zzDT() {
        CommonWalletObject commonWalletObject = new CommonWalletObject();
        commonWalletObject.getClass();
        return new zza();
    }

    public final String getId() {
        return this.zzkx;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzkx, false);
        zzd.zza(parcel, 3, this.zzbPa, false);
        zzd.zza(parcel, 4, this.name, false);
        zzd.zza(parcel, 5, this.zzbOU, false);
        zzd.zza(parcel, 6, this.zzbOW, false);
        zzd.zza(parcel, 7, this.zzbOX, false);
        zzd.zza(parcel, 8, this.zzbOY, false);
        zzd.zza(parcel, 9, this.zzbOZ, false);
        zzd.zzc(parcel, 10, this.state);
        zzd.zzc(parcel, 11, this.zzbPb, false);
        zzd.zza(parcel, 12, this.zzbPc, i, false);
        zzd.zzc(parcel, 13, this.zzbPd, false);
        zzd.zza(parcel, 14, this.zzbPe, false);
        zzd.zza(parcel, 15, this.zzbPf, false);
        zzd.zzc(parcel, 16, this.zzbPg, false);
        zzd.zza(parcel, 17, this.zzbPh);
        zzd.zzc(parcel, 18, this.zzbPi, false);
        zzd.zzc(parcel, 19, this.zzbPj, false);
        zzd.zzc(parcel, 20, this.zzbPk, false);
        zzd.zzI(parcel, zze);
    }
}
