package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.vision.barcode.Barcode.CalendarDateTime;

public class zzc implements Creator<CalendarDateTime> {
    static void zza(CalendarDateTime calendarDateTime, Parcel parcel, int i) {
        int zzaV = com.google.android.gms.common.internal.safeparcel.zzc.zzaV(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 1, calendarDateTime.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 2, calendarDateTime.year);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 3, calendarDateTime.month);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 4, calendarDateTime.day);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 5, calendarDateTime.hours);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 6, calendarDateTime.minutes);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 7, calendarDateTime.seconds);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 8, calendarDateTime.isUtc);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 9, calendarDateTime.rawValue, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziV(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznk(i);
    }

    public CalendarDateTime zziV(Parcel parcel) {
        boolean z = false;
        int zzaU = zzb.zzaU(parcel);
        String str = null;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i7 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i6 = zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    i5 = zzb.zzg(parcel, zzaT);
                    break;
                case 4:
                    i4 = zzb.zzg(parcel, zzaT);
                    break;
                case 5:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 6:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 7:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 9:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new CalendarDateTime(i7, i6, i5, i4, i3, i2, i, z, str);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public CalendarDateTime[] zznk(int i) {
        return new CalendarDateTime[i];
    }
}
