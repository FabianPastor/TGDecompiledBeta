package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;

public class zzb
  implements Parcelable.Creator<Barcode>
{
  static void zza(Barcode paramBarcode, Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramBarcode.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 2, paramBarcode.format);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 3, paramBarcode.rawValue, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 4, paramBarcode.displayValue, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 5, paramBarcode.valueFormat);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 6, paramBarcode.cornerPoints, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 7, paramBarcode.email, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 8, paramBarcode.phone, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 9, paramBarcode.sms, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 10, paramBarcode.wifi, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 11, paramBarcode.url, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 12, paramBarcode.geoPoint, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 13, paramBarcode.calendarEvent, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 14, paramBarcode.contactInfo, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 15, paramBarcode.driverLicense, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, i);
  }
  
  public Barcode[] zzabe(int paramInt)
  {
    return new Barcode[paramInt];
  }
  
  public Barcode zzsp(Parcel paramParcel)
  {
    int m = zza.zzcq(paramParcel);
    int k = 0;
    int j = 0;
    String str2 = null;
    String str1 = null;
    int i = 0;
    Point[] arrayOfPoint = null;
    Barcode.Email localEmail = null;
    Barcode.Phone localPhone = null;
    Barcode.Sms localSms = null;
    Barcode.WiFi localWiFi = null;
    Barcode.UrlBookmark localUrlBookmark = null;
    Barcode.GeoPoint localGeoPoint = null;
    Barcode.CalendarEvent localCalendarEvent = null;
    Barcode.ContactInfo localContactInfo = null;
    Barcode.DriverLicense localDriverLicense = null;
    while (paramParcel.dataPosition() < m)
    {
      int n = zza.zzcp(paramParcel);
      switch (zza.zzgv(n))
      {
      default: 
        zza.zzb(paramParcel, n);
        break;
      case 1: 
        k = zza.zzg(paramParcel, n);
        break;
      case 2: 
        j = zza.zzg(paramParcel, n);
        break;
      case 3: 
        str2 = zza.zzq(paramParcel, n);
        break;
      case 4: 
        str1 = zza.zzq(paramParcel, n);
        break;
      case 5: 
        i = zza.zzg(paramParcel, n);
        break;
      case 6: 
        arrayOfPoint = (Point[])zza.zzb(paramParcel, n, Point.CREATOR);
        break;
      case 7: 
        localEmail = (Barcode.Email)zza.zza(paramParcel, n, Barcode.Email.CREATOR);
        break;
      case 8: 
        localPhone = (Barcode.Phone)zza.zza(paramParcel, n, Barcode.Phone.CREATOR);
        break;
      case 9: 
        localSms = (Barcode.Sms)zza.zza(paramParcel, n, Barcode.Sms.CREATOR);
        break;
      case 10: 
        localWiFi = (Barcode.WiFi)zza.zza(paramParcel, n, Barcode.WiFi.CREATOR);
        break;
      case 11: 
        localUrlBookmark = (Barcode.UrlBookmark)zza.zza(paramParcel, n, Barcode.UrlBookmark.CREATOR);
        break;
      case 12: 
        localGeoPoint = (Barcode.GeoPoint)zza.zza(paramParcel, n, Barcode.GeoPoint.CREATOR);
        break;
      case 13: 
        localCalendarEvent = (Barcode.CalendarEvent)zza.zza(paramParcel, n, Barcode.CalendarEvent.CREATOR);
        break;
      case 14: 
        localContactInfo = (Barcode.ContactInfo)zza.zza(paramParcel, n, Barcode.ContactInfo.CREATOR);
        break;
      case 15: 
        localDriverLicense = (Barcode.DriverLicense)zza.zza(paramParcel, n, Barcode.DriverLicense.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zza.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new Barcode(k, j, str2, str1, i, arrayOfPoint, localEmail, localPhone, localSms, localWiFi, localUrlBookmark, localGeoPoint, localCalendarEvent, localContactInfo, localDriverLicense);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */