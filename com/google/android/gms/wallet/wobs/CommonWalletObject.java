package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

@KeepName
public class CommonWalletObject extends zzbfm {
    public static final Creator<CommonWalletObject> CREATOR = new zzb();
    String name;
    int state;
    String zzlbj;
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
    String zzwc;

    public final class zza {
        private /* synthetic */ CommonWalletObject zzlfq;

        private zza(CommonWalletObject commonWalletObject) {
            this.zzlfq = commonWalletObject;
        }

        public final CommonWalletObject zzbkc() {
            return this.zzlfq;
        }

        public final zza zznm(String str) {
            this.zzlfq.zzwc = str;
            return this;
        }
    }

    CommonWalletObject() {
        this.zzlbq = new ArrayList();
        this.zzlbs = new ArrayList();
        this.zzlbv = new ArrayList();
        this.zzlbx = new ArrayList();
        this.zzlby = new ArrayList();
        this.zzlbz = new ArrayList();
    }

    CommonWalletObject(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i, ArrayList<WalletObjectMessage> arrayList, TimeInterval timeInterval, ArrayList<LatLng> arrayList2, String str9, String str10, ArrayList<LabelValueRow> arrayList3, boolean z, ArrayList<UriData> arrayList4, ArrayList<TextModuleData> arrayList5, ArrayList<UriData> arrayList6) {
        this.zzwc = str;
        this.zzlbp = str2;
        this.name = str3;
        this.zzlbj = str4;
        this.zzlbl = str5;
        this.zzlbm = str6;
        this.zzlbn = str7;
        this.zzlbo = str8;
        this.state = i;
        this.zzlbq = arrayList;
        this.zzlbr = timeInterval;
        this.zzlbs = arrayList2;
        this.zzlbt = str9;
        this.zzlbu = str10;
        this.zzlbv = arrayList3;
        this.zzlbw = z;
        this.zzlbx = arrayList4;
        this.zzlby = arrayList5;
        this.zzlbz = arrayList6;
    }

    public static zza zzbkb() {
        return new zza();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzwc, false);
        zzbfp.zza(parcel, 3, this.zzlbp, false);
        zzbfp.zza(parcel, 4, this.name, false);
        zzbfp.zza(parcel, 5, this.zzlbj, false);
        zzbfp.zza(parcel, 6, this.zzlbl, false);
        zzbfp.zza(parcel, 7, this.zzlbm, false);
        zzbfp.zza(parcel, 8, this.zzlbn, false);
        zzbfp.zza(parcel, 9, this.zzlbo, false);
        zzbfp.zzc(parcel, 10, this.state);
        zzbfp.zzc(parcel, 11, this.zzlbq, false);
        zzbfp.zza(parcel, 12, this.zzlbr, i, false);
        zzbfp.zzc(parcel, 13, this.zzlbs, false);
        zzbfp.zza(parcel, 14, this.zzlbt, false);
        zzbfp.zza(parcel, 15, this.zzlbu, false);
        zzbfp.zzc(parcel, 16, this.zzlbv, false);
        zzbfp.zza(parcel, 17, this.zzlbw);
        zzbfp.zzc(parcel, 18, this.zzlbx, false);
        zzbfp.zzc(parcel, 19, this.zzlby, false);
        zzbfp.zzc(parcel, 20, this.zzlbz, false);
        zzbfp.zzai(parcel, zze);
    }
}
