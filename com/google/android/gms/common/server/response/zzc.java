package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.server.response.FieldMappingDictionary.Entry;
import java.util.ArrayList;

public class zzc implements Creator<FieldMappingDictionary> {
    static void zza(FieldMappingDictionary fieldMappingDictionary, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, fieldMappingDictionary.mVersionCode);
        zzb.zzc(parcel, 2, fieldMappingDictionary.zzaxm(), false);
        zzb.zza(parcel, 3, fieldMappingDictionary.zzaxn(), false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzcz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzhc(i);
    }

    public FieldMappingDictionary zzcz(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        ArrayList arrayList = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    arrayList = zza.zzc(parcel, zzcq, Entry.CREATOR);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new FieldMappingDictionary(i, arrayList, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public FieldMappingDictionary[] zzhc(int i) {
        return new FieldMappingDictionary[i];
    }
}
