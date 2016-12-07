package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze implements Creator<DataHolder> {
    static void zza(DataHolder dataHolder, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zza(parcel, 1, dataHolder.zzauo(), false);
        zzb.zza(parcel, 2, dataHolder.zzaup(), i, false);
        zzb.zzc(parcel, 3, dataHolder.getStatusCode());
        zzb.zza(parcel, 4, dataHolder.zzaui(), false);
        zzb.zzc(parcel, 1000, dataHolder.mVersionCode);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzch(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzgd(i);
    }

    public DataHolder zzch(Parcel parcel) {
        int i = 0;
        Bundle bundle = null;
        int zzcr = zza.zzcr(parcel);
        CursorWindow[] cursorWindowArr = null;
        String[] strArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    strArr = zza.zzac(parcel, zzcq);
                    break;
                case 2:
                    cursorWindowArr = (CursorWindow[]) zza.zzb(parcel, zzcq, CursorWindow.CREATOR);
                    break;
                case 3:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 4:
                    bundle = zza.zzs(parcel, zzcq);
                    break;
                case 1000:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() != zzcr) {
            throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
        }
        DataHolder dataHolder = new DataHolder(i2, strArr, cursorWindowArr, i, bundle);
        dataHolder.zzaun();
        return dataHolder;
    }

    public DataHolder[] zzgd(int i) {
        return new DataHolder[i];
    }
}
