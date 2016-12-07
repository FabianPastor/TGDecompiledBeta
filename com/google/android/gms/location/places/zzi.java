package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzi implements Creator<PlaceReport> {
    static void zza(PlaceReport placeReport, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, placeReport.mVersionCode);
        zzb.zza(parcel, 2, placeReport.getPlaceId(), false);
        zzb.zza(parcel, 3, placeReport.getTag(), false);
        zzb.zza(parcel, 4, placeReport.getSource(), false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzon(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzvp(i);
    }

    public PlaceReport zzon(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str2 = null;
        String str3 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str3 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 4:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new PlaceReport(i, str3, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public PlaceReport[] zzvp(int i) {
        return new PlaceReport[i];
    }
}
