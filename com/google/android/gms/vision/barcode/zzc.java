package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.CalendarDateTime;

public class zzc implements Creator<CalendarDateTime> {
    static void zza(CalendarDateTime calendarDateTime, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, calendarDateTime.versionCode);
        zzb.zzc(parcel, 2, calendarDateTime.year);
        zzb.zzc(parcel, 3, calendarDateTime.month);
        zzb.zzc(parcel, 4, calendarDateTime.day);
        zzb.zzc(parcel, 5, calendarDateTime.hours);
        zzb.zzc(parcel, 6, calendarDateTime.minutes);
        zzb.zzc(parcel, 7, calendarDateTime.seconds);
        zzb.zza(parcel, 8, calendarDateTime.isUtc);
        zzb.zza(parcel, 9, calendarDateTime.rawValue, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsg(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaav(i);
    }

    public CalendarDateTime[] zzaav(int i) {
        return new CalendarDateTime[i];
    }

    public CalendarDateTime zzsg(Parcel parcel) {
        boolean z = false;
        int zzcr = zza.zzcr(parcel);
        String str = null;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i7 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    i6 = zza.zzg(parcel, zzcq);
                    break;
                case 3:
                    i5 = zza.zzg(parcel, zzcq);
                    break;
                case 4:
                    i4 = zza.zzg(parcel, zzcq);
                    break;
                case 5:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 6:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 7:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 8:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 9:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new CalendarDateTime(i7, i6, i5, i4, i3, i2, i, z, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
