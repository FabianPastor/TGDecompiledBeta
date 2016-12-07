package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.CalendarDateTime;
import com.google.android.gms.vision.barcode.Barcode.CalendarEvent;

public class zzd implements Creator<CalendarEvent> {
    static void zza(CalendarEvent calendarEvent, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, calendarEvent.versionCode);
        zzb.zza(parcel, 2, calendarEvent.summary, false);
        zzb.zza(parcel, 3, calendarEvent.description, false);
        zzb.zza(parcel, 4, calendarEvent.location, false);
        zzb.zza(parcel, 5, calendarEvent.organizer, false);
        zzb.zza(parcel, 6, calendarEvent.status, false);
        zzb.zza(parcel, 7, calendarEvent.start, i, false);
        zzb.zza(parcel, 8, calendarEvent.end, i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsh(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaaw(i);
    }

    public CalendarEvent[] zzaaw(int i) {
        return new CalendarEvent[i];
    }

    public CalendarEvent zzsh(Parcel parcel) {
        CalendarDateTime calendarDateTime = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        CalendarDateTime calendarDateTime2 = null;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str5 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    str4 = zza.zzq(parcel, zzcq);
                    break;
                case 4:
                    str3 = zza.zzq(parcel, zzcq);
                    break;
                case 5:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 6:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 7:
                    calendarDateTime2 = (CalendarDateTime) zza.zza(parcel, zzcq, CalendarDateTime.CREATOR);
                    break;
                case 8:
                    calendarDateTime = (CalendarDateTime) zza.zza(parcel, zzcq, CalendarDateTime.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new CalendarEvent(i, str5, str4, str3, str2, str, calendarDateTime2, calendarDateTime);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
