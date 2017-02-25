package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze implements Creator<DataHolder> {
    static void zza(DataHolder dataHolder, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 1, dataHolder.zzxl(), false);
        zzc.zza(parcel, 2, dataHolder.zzxm(), i, false);
        zzc.zzc(parcel, 3, dataHolder.getStatusCode());
        zzc.zza(parcel, 4, dataHolder.zzxf(), false);
        zzc.zzc(parcel, 1000, dataHolder.zzaiI);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaO(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcL(i);
    }

    public DataHolder zzaO(Parcel parcel) {
        int i = 0;
        Bundle bundle = null;
        int zzaY = zzb.zzaY(parcel);
        CursorWindow[] cursorWindowArr = null;
        String[] strArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    strArr = zzb.zzC(parcel, zzaX);
                    break;
                case 2:
                    cursorWindowArr = (CursorWindow[]) zzb.zzb(parcel, zzaX, CursorWindow.CREATOR);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                case 1000:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() != zzaY) {
            throw new zza("Overread allowed size end=" + zzaY, parcel);
        }
        DataHolder dataHolder = new DataHolder(i2, strArr, cursorWindowArr, i, bundle);
        dataHolder.zzxk();
        return dataHolder;
    }

    public DataHolder[] zzcL(int i) {
        return new DataHolder[i];
    }
}
