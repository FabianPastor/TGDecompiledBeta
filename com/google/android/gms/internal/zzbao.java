package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.List;

public class zzbao implements Creator<zzban> {
    static void zza(zzban com_google_android_gms_internal_zzban, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzban.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzban.zzbEt);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzban.zzbEu, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjv(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznu(i);
    }

    public zzban zzjv(Parcel parcel) {
        boolean z = false;
        int zzaY = zzb.zzaY(parcel);
        List list = null;
        int i = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 3:
                    list = zzb.zzc(parcel, zzaX, Scope.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzban(i, z, list);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzban[] zznu(int i) {
        return new zzban[i];
    }
}
