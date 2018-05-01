package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class EventParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<EventParcel> CREATOR = new zzk();
  public final EventParams arJ;
  public final String arK;
  public final long arL;
  public final String name;
  public final int versionCode;
  
  EventParcel(int paramInt, String paramString1, EventParams paramEventParams, String paramString2, long paramLong)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.arJ = paramEventParams;
    this.arK = paramString2;
    this.arL = paramLong;
  }
  
  public EventParcel(String paramString1, EventParams paramEventParams, String paramString2, long paramLong)
  {
    this.versionCode = 1;
    this.name = paramString1;
    this.arJ = paramEventParams;
    this.arK = paramString2;
    this.arL = paramLong;
  }
  
  public String toString()
  {
    String str1 = this.arK;
    String str2 = this.name;
    String str3 = String.valueOf(this.arJ);
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