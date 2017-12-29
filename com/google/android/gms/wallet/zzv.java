package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.LabelValueRow;
import com.google.android.gms.wallet.wobs.LoyaltyPoints;
import com.google.android.gms.wallet.wobs.TextModuleData;
import com.google.android.gms.wallet.wobs.TimeInterval;
import com.google.android.gms.wallet.wobs.UriData;
import com.google.android.gms.wallet.wobs.WalletObjectMessage;
import java.util.ArrayList;

public final class zzv implements Creator<LoyaltyWalletObject> {
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
        String str9 = null;
        String str10 = null;
        int i = 0;
        ArrayList arrayList = new ArrayList();
        TimeInterval timeInterval = null;
        ArrayList arrayList2 = new ArrayList();
        String str11 = null;
        String str12 = null;
        ArrayList arrayList3 = new ArrayList();
        boolean z = false;
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        LoyaltyPoints loyaltyPoints = null;
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
                    str9 = zzbfn.zzq(parcel, readInt);
                    break;
                case 11:
                    str10 = zzbfn.zzq(parcel, readInt);
                    break;
                case 12:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 13:
                    arrayList = zzbfn.zzc(parcel, readInt, WalletObjectMessage.CREATOR);
                    break;
                case 14:
                    timeInterval = (TimeInterval) zzbfn.zza(parcel, readInt, TimeInterval.CREATOR);
                    break;
                case 15:
                    arrayList2 = zzbfn.zzc(parcel, readInt, LatLng.CREATOR);
                    break;
                case 16:
                    str11 = zzbfn.zzq(parcel, readInt);
                    break;
                case 17:
                    str12 = zzbfn.zzq(parcel, readInt);
                    break;
                case 18:
                    arrayList3 = zzbfn.zzc(parcel, readInt, LabelValueRow.CREATOR);
                    break;
                case 19:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 20:
                    arrayList4 = zzbfn.zzc(parcel, readInt, UriData.CREATOR);
                    break;
                case 21:
                    arrayList5 = zzbfn.zzc(parcel, readInt, TextModuleData.CREATOR);
                    break;
                case 22:
                    arrayList6 = zzbfn.zzc(parcel, readInt, UriData.CREATOR);
                    break;
                case 23:
                    loyaltyPoints = (LoyaltyPoints) zzbfn.zza(parcel, readInt, LoyaltyPoints.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LoyaltyWalletObject(str, str2, str3, str4, str5, str6, str7, str8, str9, str10, i, arrayList, timeInterval, arrayList2, str11, str12, arrayList3, z, arrayList4, arrayList5, arrayList6, loyaltyPoints);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LoyaltyWalletObject[i];
    }
}
