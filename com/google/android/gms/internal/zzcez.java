package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzcez
  extends zza
{
  public static final Parcelable.Creator<zzcez> CREATOR = new zzcfa();
  public final String name;
  public final zzcew zzbpM;
  public final long zzbpN;
  public final String zzbpc;
  
  zzcez(zzcez paramzzcez, long paramLong)
  {
    zzbo.zzu(paramzzcez);
    this.name = paramzzcez.name;
    this.zzbpM = paramzzcez.zzbpM;
    this.zzbpc = paramzzcez.zzbpc;
    this.zzbpN = paramLong;
  }
  
  public zzcez(String paramString1, zzcew paramzzcew, String paramString2, long paramLong)
  {
    this.name = paramString1;
    this.zzbpM = paramzzcew;
    this.zzbpc = paramString2;
    this.zzbpN = paramLong;
  }
  
  public final String toString()
  {
    String str1 = this.zzbpc;
    String str2 = this.name;
    String str3 = String.valueOf(this.zzbpM);
    return String.valueOf(str1).length() + 21 + String.valueOf(str2).length() + String.valueOf(str3).length() + "origin=" + str1 + ",name=" + str2 + ",params=" + str3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.name, false);
    zzd.zza(paramParcel, 3, this.zzbpM, paramInt, false);
    zzd.zza(paramParcel, 4, this.zzbpc, false);
    zzd.zza(paramParcel, 5, this.zzbpN);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcez.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */