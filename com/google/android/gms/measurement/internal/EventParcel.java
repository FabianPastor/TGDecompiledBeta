package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class EventParcel
  extends AbstractSafeParcelable
{
  public static final zzk CREATOR = new zzk();
  public final String aoA;
  public final long aoB;
  public final EventParams aoz;
  public final String name;
  public final int versionCode;
  
  EventParcel(int paramInt, String paramString1, EventParams paramEventParams, String paramString2, long paramLong)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.aoz = paramEventParams;
    this.aoA = paramString2;
    this.aoB = paramLong;
  }
  
  public EventParcel(String paramString1, EventParams paramEventParams, String paramString2, long paramLong)
  {
    this.versionCode = 1;
    this.name = paramString1;
    this.aoz = paramEventParams;
    this.aoA = paramString2;
    this.aoB = paramLong;
  }
  
  public String toString()
  {
    String str1 = this.aoA;
    String str2 = this.name;
    String str3 = String.valueOf(this.aoz);
    return String.valueOf(str1).length() + 21 + String.valueOf(str2).length() + String.valueOf(str3).length() + "origin=" + str1 + ",name=" + str2 + ",params=" + str3;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzk.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/EventParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */