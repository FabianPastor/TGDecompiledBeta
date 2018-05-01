package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatq
  extends zza
{
  public static final Parcelable.Creator<zzatq> CREATOR = new zzatr();
  public final String name;
  public final String zzbqW;
  public final zzato zzbrH;
  public final long zzbrI;
  
  zzatq(zzatq paramzzatq, long paramLong)
  {
    zzac.zzw(paramzzatq);
    this.name = paramzzatq.name;
    this.zzbrH = paramzzatq.zzbrH;
    this.zzbqW = paramzzatq.zzbqW;
    this.zzbrI = paramLong;
  }
  
  public zzatq(String paramString1, zzato paramzzato, String paramString2, long paramLong)
  {
    this.name = paramString1;
    this.zzbrH = paramzzato;
    this.zzbqW = paramString2;
    this.zzbrI = paramLong;
  }
  
  public String toString()
  {
    String str1 = this.zzbqW;
    String str2 = this.name;
    String str3 = String.valueOf(this.zzbrH);
    return String.valueOf(str1).length() + 21 + String.valueOf(str2).length() + String.valueOf(str3).length() + "origin=" + str1 + ",name=" + str2 + ",params=" + str3;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzatr.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */