package com.google.android.gms.internal.measurement;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzeu
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzeu> CREATOR = new zzev();
  public final String name;
  public final String zzaek;
  public final zzer zzafo;
  public final long zzafz;
  
  public zzeu(String paramString1, zzer paramzzer, String paramString2, long paramLong)
  {
    this.name = paramString1;
    this.zzafo = paramzzer;
    this.zzaek = paramString2;
    this.zzafz = paramLong;
  }
  
  public final String toString()
  {
    String str1 = this.zzaek;
    String str2 = this.name;
    String str3 = String.valueOf(this.zzafo);
    return String.valueOf(str1).length() + 21 + String.valueOf(str2).length() + String.valueOf(str3).length() + "origin=" + str1 + ",name=" + str2 + ",params=" + str3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.name, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.zzafo, paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzaek, false);
    SafeParcelWriter.writeLong(paramParcel, 5, this.zzafz);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzeu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */