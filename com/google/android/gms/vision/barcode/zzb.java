package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
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
        int zzcs = com.google.android.gms.common.internal.safeparcel.zzb.zzcs(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, barcode.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 2, barcode.format);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, barcode.rawValue, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, barcode.displayValue, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 5, barcode.valueFormat);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, barcode.cornerPoints, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, barcode.email, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, barcode.phone, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 9, barcode.sms, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 10, barcode.wifi, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 11, barcode.url, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 12, barcode.geoPoint, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 13, barcode.calendarEvent, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 14, barcode.contactInfo, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 15, barcode.driverLicense, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsf(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaau(i);
    }

    public Barcode[] zzaau(int i) {
        return new Barcode[i];
    }

    public Barcode zzsf(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
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
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 4:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 5:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 6:
                    pointArr = (Point[]) zza.zzb(parcel, zzcq, Point.CREATOR);
                    break;
                case 7:
                    email = (Email) zza.zza(parcel, zzcq, Email.CREATOR);
                    break;
                case 8:
                    phone = (Phone) zza.zza(parcel, zzcq, Phone.CREATOR);
                    break;
                case 9:
                    sms = (Sms) zza.zza(parcel, zzcq, Sms.CREATOR);
                    break;
                case 10:
                    wiFi = (WiFi) zza.zza(parcel, zzcq, WiFi.CREATOR);
                    break;
                case 11:
                    urlBookmark = (UrlBookmark) zza.zza(parcel, zzcq, UrlBookmark.CREATOR);
                    break;
                case 12:
                    geoPoint = (GeoPoint) zza.zza(parcel, zzcq, GeoPoint.CREATOR);
                    break;
                case 13:
                    calendarEvent = (CalendarEvent) zza.zza(parcel, zzcq, CalendarEvent.CREATOR);
                    break;
                case 14:
                    contactInfo = (ContactInfo) zza.zza(parcel, zzcq, ContactInfo.CREATOR);
                    break;
                case 15:
                    driverLicense = (DriverLicense) zza.zza(parcel, zzcq, DriverLicense.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Barcode(i, i2, str, str2, i3, pointArr, email, phone, sms, wiFi, urlBookmark, geoPoint, calendarEvent, contactInfo, driverLicense);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
