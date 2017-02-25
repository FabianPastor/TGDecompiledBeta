package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.vision.barcode.Barcode.CalendarDateTime;

public class zzc implements Creator<CalendarDateTime> {
    static void zza(CalendarDateTime calendarDateTime, Parcel parcel, int i) {
        int zzaZ = com.google.android.gms.common.internal.safeparcel.zzc.zzaZ(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 1, calendarDateTime.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 2, calendarDateTime.year);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 3, calendarDateTime.month);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 4, calendarDateTime.day);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 5, calendarDateTime.hours);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 6, calendarDateTime.minutes);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 7, calendarDateTime.seconds);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 8, calendarDateTime.isUtc);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 9, calendarDateTime.rawValue, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjB(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznV(i);
    }

    public CalendarDateTime zzjB(Parcel parcel) {
        boolean z = false;
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i7 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i6 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    i5 = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    i4 = zzb.zzg(parcel, zzaX);
                    break;
                case 5:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 6:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 7:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 9:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new CalendarDateTime(i7, i6, i5, i4, i3, i2, i, z, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public CalendarDateTime[] zznV(int i) {
        return new CalendarDateTime[i];
    }
}
