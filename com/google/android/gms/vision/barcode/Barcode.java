package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class Barcode
  extends com.google.android.gms.common.internal.safeparcel.zza
{
  public static final int ALL_FORMATS = 0;
  public static final int AZTEC = 4096;
  public static final int CALENDAR_EVENT = 11;
  public static final int CODABAR = 8;
  public static final int CODE_128 = 1;
  public static final int CODE_39 = 2;
  public static final int CODE_93 = 4;
  public static final int CONTACT_INFO = 1;
  public static final Parcelable.Creator<Barcode> CREATOR = new zzb();
  public static final int DATA_MATRIX = 16;
  public static final int DRIVER_LICENSE = 12;
  public static final int EAN_13 = 32;
  public static final int EAN_8 = 64;
  public static final int EMAIL = 2;
  public static final int GEO = 10;
  public static final int ISBN = 3;
  public static final int ITF = 128;
  public static final int PDF417 = 2048;
  public static final int PHONE = 4;
  public static final int PRODUCT = 5;
  public static final int QR_CODE = 256;
  public static final int SMS = 6;
  public static final int TEXT = 7;
  public static final int UPC_A = 512;
  public static final int UPC_E = 1024;
  public static final int URL = 8;
  public static final int WIFI = 9;
  public CalendarEvent calendarEvent;
  public ContactInfo contactInfo;
  public Point[] cornerPoints;
  public String displayValue;
  public DriverLicense driverLicense;
  public Email email;
  public int format;
  public GeoPoint geoPoint;
  public Phone phone;
  public String rawValue;
  public Sms sms;
  public UrlBookmark url;
  public int valueFormat;
  public WiFi wifi;
  
  public Barcode() {}
  
  public Barcode(int paramInt1, String paramString1, String paramString2, int paramInt2, Point[] paramArrayOfPoint, Email paramEmail, Phone paramPhone, Sms paramSms, WiFi paramWiFi, UrlBookmark paramUrlBookmark, GeoPoint paramGeoPoint, CalendarEvent paramCalendarEvent, ContactInfo paramContactInfo, DriverLicense paramDriverLicense)
  {
    this.format = paramInt1;
    this.rawValue = paramString1;
    this.displayValue = paramString2;
    this.valueFormat = paramInt2;
    this.cornerPoints = paramArrayOfPoint;
    this.email = paramEmail;
    this.phone = paramPhone;
    this.sms = paramSms;
    this.wifi = paramWiFi;
    this.url = paramUrlBookmark;
    this.geoPoint = paramGeoPoint;
    this.calendarEvent = paramCalendarEvent;
    this.contactInfo = paramContactInfo;
    this.driverLicense = paramDriverLicense;
  }
  
  public Rect getBoundingBox()
  {
    int k = Integer.MAX_VALUE;
    int j = Integer.MIN_VALUE;
    int i = 0;
    int m = Integer.MIN_VALUE;
    int n = Integer.MAX_VALUE;
    while (i < this.cornerPoints.length)
    {
      Point localPoint = this.cornerPoints[i];
      n = Math.min(n, localPoint.x);
      m = Math.max(m, localPoint.x);
      k = Math.min(k, localPoint.y);
      j = Math.max(j, localPoint.y);
      i += 1;
    }
    return new Rect(n, k, m, j);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, this.format);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.rawValue, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.displayValue, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 5, this.valueFormat);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, this.cornerPoints, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, this.email, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.phone, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 9, this.sms, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 10, this.wifi, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 11, this.url, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 12, this.geoPoint, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 13, this.calendarEvent, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 14, this.contactInfo, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 15, this.driverLicense, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, i);
  }
  
  public static class Address
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<Address> CREATOR = new zza();
    public static final int HOME = 2;
    public static final int UNKNOWN = 0;
    public static final int WORK = 1;
    public String[] addressLines;
    public int type;
    
    public Address() {}
    
    public Address(int paramInt, String[] paramArrayOfString)
    {
      this.type = paramInt;
      this.addressLines = paramArrayOfString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, this.type);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.addressLines, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class CalendarDateTime
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<CalendarDateTime> CREATOR = new zzd();
    public int day;
    public int hours;
    public boolean isUtc;
    public int minutes;
    public int month;
    public String rawValue;
    public int seconds;
    public int year;
    
    public CalendarDateTime() {}
    
    public CalendarDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, String paramString)
    {
      this.year = paramInt1;
      this.month = paramInt2;
      this.day = paramInt3;
      this.hours = paramInt4;
      this.minutes = paramInt5;
      this.seconds = paramInt6;
      this.isUtc = paramBoolean;
      this.rawValue = paramString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, this.year);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 3, this.month);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 4, this.day);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 5, this.hours);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 6, this.minutes);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 7, this.seconds);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.isUtc);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 9, this.rawValue, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class CalendarEvent
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<CalendarEvent> CREATOR = new zze();
    public String description;
    public Barcode.CalendarDateTime end;
    public String location;
    public String organizer;
    public Barcode.CalendarDateTime start;
    public String status;
    public String summary;
    
    public CalendarEvent() {}
    
    public CalendarEvent(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Barcode.CalendarDateTime paramCalendarDateTime1, Barcode.CalendarDateTime paramCalendarDateTime2)
    {
      this.summary = paramString1;
      this.description = paramString2;
      this.location = paramString3;
      this.organizer = paramString4;
      this.status = paramString5;
      this.start = paramCalendarDateTime1;
      this.end = paramCalendarDateTime2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.summary, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.description, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.location, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, this.organizer, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, this.status, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, this.start, paramInt, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.end, paramInt, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, i);
    }
  }
  
  public static class ContactInfo
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<ContactInfo> CREATOR = new zzf();
    public Barcode.Address[] addresses;
    public Barcode.Email[] emails;
    public Barcode.PersonName name;
    public String organization;
    public Barcode.Phone[] phones;
    public String title;
    public String[] urls;
    
    public ContactInfo() {}
    
    public ContactInfo(Barcode.PersonName paramPersonName, String paramString1, String paramString2, Barcode.Phone[] paramArrayOfPhone, Barcode.Email[] paramArrayOfEmail, String[] paramArrayOfString, Barcode.Address[] paramArrayOfAddress)
    {
      this.name = paramPersonName;
      this.organization = paramString1;
      this.title = paramString2;
      this.phones = paramArrayOfPhone;
      this.emails = paramArrayOfEmail;
      this.urls = paramArrayOfString;
      this.addresses = paramArrayOfAddress;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.name, paramInt, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.organization, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.title, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, this.phones, paramInt, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, this.emails, paramInt, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, this.urls, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.addresses, paramInt, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, i);
    }
  }
  
  public static class DriverLicense
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<DriverLicense> CREATOR = new zzg();
    public String addressCity;
    public String addressState;
    public String addressStreet;
    public String addressZip;
    public String birthDate;
    public String documentType;
    public String expiryDate;
    public String firstName;
    public String gender;
    public String issueDate;
    public String issuingCountry;
    public String lastName;
    public String licenseNumber;
    public String middleName;
    
    public DriverLicense() {}
    
    public DriverLicense(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, String paramString13, String paramString14)
    {
      this.documentType = paramString1;
      this.firstName = paramString2;
      this.middleName = paramString3;
      this.lastName = paramString4;
      this.gender = paramString5;
      this.addressStreet = paramString6;
      this.addressCity = paramString7;
      this.addressState = paramString8;
      this.addressZip = paramString9;
      this.licenseNumber = paramString10;
      this.issueDate = paramString11;
      this.expiryDate = paramString12;
      this.birthDate = paramString13;
      this.issuingCountry = paramString14;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.documentType, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.firstName, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.middleName, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, this.lastName, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, this.gender, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, this.addressStreet, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.addressCity, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 9, this.addressState, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 10, this.addressZip, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 11, this.licenseNumber, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 12, this.issueDate, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 13, this.expiryDate, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 14, this.birthDate, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 15, this.issuingCountry, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class Email
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<Email> CREATOR = new zzh();
    public static final int HOME = 2;
    public static final int UNKNOWN = 0;
    public static final int WORK = 1;
    public String address;
    public String body;
    public String subject;
    public int type;
    
    public Email() {}
    
    public Email(int paramInt, String paramString1, String paramString2, String paramString3)
    {
      this.type = paramInt;
      this.address = paramString1;
      this.subject = paramString2;
      this.body = paramString3;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, this.type);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.address, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.subject, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, this.body, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class GeoPoint
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<GeoPoint> CREATOR = new zzi();
    public double lat;
    public double lng;
    
    public GeoPoint() {}
    
    public GeoPoint(double paramDouble1, double paramDouble2)
    {
      this.lat = paramDouble1;
      this.lng = paramDouble2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.lat);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.lng);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class PersonName
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<PersonName> CREATOR = new zzj();
    public String first;
    public String formattedName;
    public String last;
    public String middle;
    public String prefix;
    public String pronunciation;
    public String suffix;
    
    public PersonName() {}
    
    public PersonName(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
    {
      this.formattedName = paramString1;
      this.pronunciation = paramString2;
      this.prefix = paramString3;
      this.first = paramString4;
      this.middle = paramString5;
      this.last = paramString6;
      this.suffix = paramString7;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.formattedName, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.pronunciation, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.prefix, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, this.first, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, this.middle, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, this.last, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.suffix, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class Phone
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<Phone> CREATOR = new zzk();
    public static final int FAX = 3;
    public static final int HOME = 2;
    public static final int MOBILE = 4;
    public static final int UNKNOWN = 0;
    public static final int WORK = 1;
    public String number;
    public int type;
    
    public Phone() {}
    
    public Phone(int paramInt, String paramString)
    {
      this.type = paramInt;
      this.number = paramString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, this.type);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.number, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class Sms
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<Sms> CREATOR = new zzl();
    public String message;
    public String phoneNumber;
    
    public Sms() {}
    
    public Sms(String paramString1, String paramString2)
    {
      this.message = paramString1;
      this.phoneNumber = paramString2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.message, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.phoneNumber, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class UrlBookmark
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<UrlBookmark> CREATOR = new zzm();
    public String title;
    public String url;
    
    public UrlBookmark() {}
    
    public UrlBookmark(String paramString1, String paramString2)
    {
      this.title = paramString1;
      this.url = paramString2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.title, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.url, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
  
  public static class WiFi
    extends com.google.android.gms.common.internal.safeparcel.zza
  {
    public static final Parcelable.Creator<WiFi> CREATOR = new zzn();
    public static final int OPEN = 1;
    public static final int WEP = 3;
    public static final int WPA = 2;
    public int encryptionType;
    public String password;
    public String ssid;
    
    public WiFi() {}
    
    public WiFi(String paramString1, String paramString2, int paramInt)
    {
      this.ssid = paramString1;
      this.password = paramString2;
      this.encryptionType = paramInt;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.ssid, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.password, false);
      com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 4, this.encryptionType);
      com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/Barcode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */