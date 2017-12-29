package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public final class zzb implements Creator<CommonWalletObject> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        int i = 0;
        ArrayList arrayList = new ArrayList();
        TimeInterval timeInterval = null;
        ArrayList arrayList2 = new ArrayList();
        String str9 = null;
        String str10 = null;
        ArrayList arrayList3 = new ArrayList();
        boolean z = false;
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 5:
                    str4 = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    str5 = zzbfn.zzq(parcel, readInt);
                    break;
                case 7:
                    str6 = zzbfn.zzq(parcel, readInt);
                    break;
                case 8:
                    str7 = zzbfn.zzq(parcel, readInt);
                    break;
                case 9:
                    str8 = zzbfn.zzq(parcel, readInt);
                    break;
                case 10:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 11:
                    arrayList = zzbfn.zzc(parcel, readInt, WalletObjectMessage.CREATOR);
                    break;
                case 12:
                    timeInterval = (TimeInterval) zzbfn.zza(parcel, readInt, TimeInterval.CREATOR);
                    break;
                case 13:
                    arrayList2 = zzbfn.zzc(parcel, readInt, LatLng.CREATOR);
                    break;
                case 14:
                    str9 = zzbfn.zzq(parcel, readInt);
                    break;
                case 15:
                    str10 = zzbfn.zzq(parcel, readInt);
                    break;
                case 16:
                    arrayList3 = zzbfn.zzc(parcel, readInt, LabelValueRow.CREATOR);
                    break;
                case 17:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 18:
                    arrayList4 = zzbfn.zzc(parcel, readInt, UriData.CREATOR);
                    break;
                case 19:
                    arrayList5 = zzbfn.zzc(parcel, readInt, TextModuleData.CREATOR);
                    break;
                case 20:
                    arrayList6 = zzbfn.zzc(parcel, readInt, UriData.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new CommonWalletObject(str, str2, str3, str4, str5, str6, str7, str8, i, arrayList, timeInterval, arrayList2, str9, str10, arrayList3, z, arrayList4, arrayList5, arrayList6);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new CommonWalletObject[i];
    }
}
