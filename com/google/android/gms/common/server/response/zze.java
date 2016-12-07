package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze implements Creator<SafeParcelResponse> {
    static void zza(SafeParcelResponse safeParcelResponse, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, safeParcelResponse.getVersionCode());
        zzb.zza(parcel, 2, safeParcelResponse.zzaxp(), false);
        zzb.zza(parcel, 3, safeParcelResponse.zzaxq(), i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzdb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzhe(i);
    }

    public SafeParcelResponse zzdb(Parcel parcel) {
        FieldMappingDictionary fieldMappingDictionary = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        Parcel parcel2 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    parcel2 = zza.zzaf(parcel, zzcq);
                    break;
                case 3:
                    fieldMappingDictionary = (FieldMappingDictionary) zza.zza(parcel, zzcq, FieldMappingDictionary.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new SafeParcelResponse(i, parcel2, fieldMappingDictionary);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public SafeParcelResponse[] zzhe(int i) {
        return new SafeParcelResponse[i];
    }
}
