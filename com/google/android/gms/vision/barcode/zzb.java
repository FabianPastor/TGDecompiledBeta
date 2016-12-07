package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.CalendarEvent;
import com.google.android.gms.vision.barcode.Barcode.ContactInfo;
import com.google.android.gms.vision.barcode.Barcode.DriverLicense;
import com.google.android.gms.vision.barcode.Barcode.Email;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;
import com.google.android.gms.vision.barcode.Barcode.Phone;
import com.google.android.gms.vision.barcode.Barcode.Sms;
import com.google.android.gms.vision.barcode.Barcode.UrlBookmark;
import com.google.android.gms.vision.barcode.Barcode.WiFi;

public class zzb implements Creator<Barcode> {
    static void zza(Barcode barcode, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, barcode.versionCode);
        zzc.zzc(parcel, 2, barcode.format);
        zzc.zza(parcel, 3, barcode.rawValue, false);
        zzc.zza(parcel, 4, barcode.displayValue, false);
        zzc.zzc(parcel, 5, barcode.valueFormat);
        zzc.zza(parcel, 6, barcode.cornerPoints, i, false);
        zzc.zza(parcel, 7, barcode.email, i, false);
        zzc.zza(parcel, 8, barcode.phone, i, false);
        zzc.zza(parcel, 9, barcode.sms, i, false);
        zzc.zza(parcel, 10, barcode.wifi, i, false);
        zzc.zza(parcel, 11, barcode.url, i, false);
        zzc.zza(parcel, 12, barcode.geoPoint, i, false);
        zzc.zza(parcel, 13, barcode.calendarEvent, i, false);
        zzc.zza(parcel, 14, barcode.contactInfo, i, false);
        zzc.zza(parcel, 15, barcode.driverLicense, i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziU(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznj(i);
    }

    public Barcode zziU(Parcel parcel) {
        int zzaU = com.google.android.gms.common.internal.safeparcel.zzb.zzaU(parcel);
        int i = 0;
        int i2 = 0;
        String str = null;
        String str2 = null;
        int i3 = 0;
        Point[] pointArr = null;
        Email email = null;
        Phone phone = null;
        Sms sms = null;
        WiFi wiFi = null;
        UrlBookmark urlBookmark = null;
        GeoPoint geoPoint = null;
        CalendarEvent calendarEvent = null;
        ContactInfo contactInfo = null;
        DriverLicense driverLicense = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = com.google.android.gms.common.internal.safeparcel.zzb.zzaT(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzcW(zzaT)) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i2 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    str = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaT);
                    break;
                case 4:
                    str2 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaT);
                    break;
                case 5:
                    i3 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 6:
                    pointArr = (Point[]) com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaT, Point.CREATOR);
                    break;
                case 7:
                    email = (Email) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, Email.CREATOR);
                    break;
                case 8:
                    phone = (Phone) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, Phone.CREATOR);
                    break;
                case 9:
                    sms = (Sms) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, Sms.CREATOR);
                    break;
                case 10:
                    wiFi = (WiFi) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, WiFi.CREATOR);
                    break;
                case 11:
                    urlBookmark = (UrlBookmark) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, UrlBookmark.CREATOR);
                    break;
                case 12:
                    geoPoint = (GeoPoint) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, GeoPoint.CREATOR);
                    break;
                case 13:
                    calendarEvent = (CalendarEvent) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, CalendarEvent.CREATOR);
                    break;
                case 14:
                    contactInfo = (ContactInfo) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, ContactInfo.CREATOR);
                    break;
                case 15:
                    driverLicense = (DriverLicense) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, DriverLicense.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new Barcode(i, i2, str, str2, i3, pointArr, email, phone, sms, wiFi, urlBookmark, geoPoint, calendarEvent, contactInfo, driverLicense);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public Barcode[] zznj(int i) {
        return new Barcode[i];
    }
}
