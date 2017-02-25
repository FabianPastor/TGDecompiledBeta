package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzacp.zza;
import java.util.ArrayList;

public class zzacq implements Creator<zzacp> {
    static void zza(zzacp com_google_android_gms_internal_zzacp, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacp.zzaiI);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzacp.zzyq(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbc(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdg(i);
    }

    public zzacp zzbc(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        ArrayList arrayList = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    arrayList = zzb.zzc(parcel, zzaX, zza.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzacp(i, arrayList);
        }
        throw new zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzacp[] zzdg(int i) {
        return new zzacp[i];
    }
}
