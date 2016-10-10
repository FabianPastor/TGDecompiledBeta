package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class Barcode
  extends AbstractSafeParcelable
{
  public static final int ALL_FORMATS = 0;
  public static final int AZTEC = 4096;
  public static final int CALENDAR_EVENT = 11;
  public static final int CODABAR = 8;
  public static final int CODE_128 = 1;
  public static final int CODE_39 = 2;
  public static final int CODE_93 = 4;
  public static final int CONTACT_INFO = 1;
  public static final zzb CREATOR = new zzb();
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
  final int versionCode;
  public WiFi wifi;
  
  public Barcode()
  {
    this.versionCode = 1;
  }
  
  public Barcode(int paramInt1, int paramInt2, String paramString1, String paramString2, int paramInt3, Point[] paramArrayOfPoint, Email paramEmail, Phone paramPhone, Sms paramSms, WiFi paramWiFi, UrlBookmark paramUrlBookmark, GeoPoint paramGeoPoint, CalendarEvent paramCalendarEvent, ContactInfo paramContactInfo, DriverLicense paramDriverLicense)
  {
    this.versionCode = paramInt1;
    this.format = paramInt2;
    this.rawValue = paramString1;
    this.displayValue = paramString2;
    this.valueFormat = paramInt3;
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
    zzb localzzb = CREATOR;
    zzb.zza(this, paramParcel, paramInt);
  }
  
  public static class Address
    extends AbstractSafeParcelable
  {
    public static final zza CREATOR = new zza();
    public static final int HOME = 2;
    public static final int UNKNOWN = 0;
    public static final int WORK = 1;
    public String[] addressLines;
    public int type;
    final int versionCode;
    
    public Address()
    {
      this.versionCode = 1;
    }
    
    public Address(int paramInt1, int paramInt2, String[] paramArrayOfString)
    {
      this.versionCode = paramInt1;
      this.type = paramInt2;
      this.addressLines = paramArrayOfString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zza localzza = CREATOR;
      zza.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class CalendarDateTime
    extends AbstractSafeParcelable
  {
    public static final zzc CREATOR = new zzc();
    public int day;
    public int hours;
    public boolean isUtc;
    public int minutes;
    public int month;
    public String rawValue;
    public int seconds;
    final int versionCode;
    public int year;
    
    public CalendarDateTime()
    {
      this.versionCode = 1;
    }
    
    public CalendarDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, String paramString)
    {
      this.versionCode = paramInt1;
      this.year = paramInt2;
      this.month = paramInt3;
      this.day = paramInt4;
      this.hours = paramInt5;
      this.minutes = paramInt6;
      this.seconds = paramInt7;
      this.isUtc = paramBoolean;
      this.rawValue = paramString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzc localzzc = CREATOR;
      zzc.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class CalendarEvent
    extends AbstractSafeParcelable
  {
    public static final zzd CREATOR = new zzd();
    public String description;
    public Barcode.CalendarDateTime end;
    public String location;
    public String organizer;
    public Barcode.CalendarDateTime start;
    public String status;
    public String summary;
    final int versionCode;
    
    public CalendarEvent()
    {
      this.versionCode = 1;
    }
    
    public CalendarEvent(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Barcode.CalendarDateTime paramCalendarDateTime1, Barcode.CalendarDateTime paramCalendarDateTime2)
    {
      this.versionCode = paramInt;
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
      zzd localzzd = CREATOR;
      zzd.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class ContactInfo
    extends AbstractSafeParcelable
  {
    public static final zze CREATOR = new zze();
    public Barcode.Address[] addresses;
    public Barcode.Email[] emails;
    public Barcode.PersonName name;
    public String organization;
    public Barcode.Phone[] phones;
    public String title;
    public String[] urls;
    final int versionCode;
    
    public ContactInfo()
    {
      this.versionCode = 1;
    }
    
    public ContactInfo(int paramInt, Barcode.PersonName paramPersonName, String paramString1, String paramString2, Barcode.Phone[] paramArrayOfPhone, Barcode.Email[] paramArrayOfEmail, String[] paramArrayOfString, Barcode.Address[] paramArrayOfAddress)
    {
      this.versionCode = paramInt;
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
      zze localzze = CREATOR;
      zze.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class DriverLicense
    extends AbstractSafeParcelable
  {
    public static final zzf CREATOR = new zzf();
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
    final int versionCode;
    
    public DriverLicense()
    {
      this.versionCode = 1;
    }
    
    public DriverLicense(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, String paramString13, String paramString14)
    {
      this.versionCode = paramInt;
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
      zzf localzzf = CREATOR;
      zzf.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class Email
    extends AbstractSafeParcelable
  {
    public static final zzg CREATOR = new zzg();
    public static final int HOME = 2;
    public static final int UNKNOWN = 0;
    public static final int WORK = 1;
    public String address;
    public String body;
    public String subject;
    public int type;
    final int versionCode;
    
    public Email()
    {
      this.versionCode = 1;
    }
    
    public Email(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3)
    {
      this.versionCode = paramInt1;
      this.type = paramInt2;
      this.address = paramString1;
      this.subject = paramString2;
      this.body = paramString3;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzg localzzg = CREATOR;
      zzg.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class GeoPoint
    extends AbstractSafeParcelable
  {
    public static final zzh CREATOR = new zzh();
    public double lat;
    public double lng;
    final int versionCode;
    
    public GeoPoint()
    {
      this.versionCode = 1;
    }
    
    public GeoPoint(int paramInt, double paramDouble1, double paramDouble2)
    {
      this.versionCode = paramInt;
      this.lat = paramDouble1;
      this.lng = paramDouble2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzh localzzh = CREATOR;
      zzh.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class PersonName
    extends AbstractSafeParcelable
  {
    public static final zzi CREATOR = new zzi();
    public String first;
    public String formattedName;
    public String last;
    public String middle;
    public String prefix;
    public String pronunciation;
    public String suffix;
    final int versionCode;
    
    public PersonName()
    {
      this.versionCode = 1;
    }
    
    public PersonName(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
    {
      this.versionCode = paramInt;
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
      zzi localzzi = CREATOR;
      zzi.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class Phone
    extends AbstractSafeParcelable
  {
    public static final zzj CREATOR = new zzj();
    public static final int FAX = 3;
    public static final int HOME = 2;
    public static final int MOBILE = 4;
    public static final int UNKNOWN = 0;
    public static final int WORK = 1;
    public String number;
    public int type;
    final int versionCode;
    
    public Phone()
    {
      this.versionCode = 1;
    }
    
    public Phone(int paramInt1, int paramInt2, String paramString)
    {
      this.versionCode = paramInt1;
      this.type = paramInt2;
      this.number = paramString;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzj localzzj = CREATOR;
      zzj.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class Sms
    extends AbstractSafeParcelable
  {
    public static final zzk CREATOR = new zzk();
    public String message;
    public String phoneNumber;
    final int versionCode;
    
    public Sms()
    {
      this.versionCode = 1;
    }
    
    public Sms(int paramInt, String paramString1, String paramString2)
    {
      this.versionCode = paramInt;
      this.message = paramString1;
      this.phoneNumber = paramString2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzk localzzk = CREATOR;
      zzk.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class UrlBookmark
    extends AbstractSafeParcelable
  {
    public static final zzl CREATOR = new zzl();
    public String title;
    public String url;
    final int versionCode;
    
    public UrlBookmark()
    {
      this.versionCode = 1;
    }
    
    public UrlBookmark(int paramInt, String paramString1, String paramString2)
    {
      this.versionCode = paramInt;
      this.title = paramString1;
      this.url = paramString2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzl localzzl = CREATOR;
      zzl.zza(this, paramParcel, paramInt);
    }
  }
  
  public static class WiFi
    extends AbstractSafeParcelable
  {
    public static final zzm CREATOR = new zzm();
    public static final int OPEN = 1;
    public static final int WEP = 3;
    public static final int WPA = 2;
    public int encryptionType;
    public String password;
    public String ssid;
    final int versionCode;
    
    public WiFi()
    {
      this.versionCode = 1;
    }
    
    public WiFi(int paramInt1, String paramString1, String paramString2, int paramInt2)
    {
      this.versionCode = paramInt1;
      this.ssid = paramString1;
      this.password = paramString2;
      this.encryptionType = paramInt2;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      zzm localzzm = CREATOR;
      zzm.zza(this, paramParcel, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/Barcode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */