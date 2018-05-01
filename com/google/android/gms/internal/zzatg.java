package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatg
  extends zza
{
  public static final Parcelable.Creator<zzatg> CREATOR = new zzath();
  public String packageName;
  public final int versionCode;
  public String zzbqW;
  public zzauq zzbqX;
  public long zzbqY;
  public boolean zzbqZ;
  public String zzbra;
  public zzatq zzbrb;
  public long zzbrc;
  public zzatq zzbrd;
  public long zzbre;
  public zzatq zzbrf;
  
  zzatg(int paramInt, String paramString1, String paramString2, zzauq paramzzauq, long paramLong1, boolean paramBoolean, String paramString3, zzatq paramzzatq1, long paramLong2, zzatq paramzzatq2, long paramLong3, zzatq paramzzatq3)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.zzbqW = paramString2;
    this.zzbqX = paramzzauq;
    this.zzbqY = paramLong1;
    this.zzbqZ = paramBoolean;
    this.zzbra = paramString3;
    this.zzbrb = paramzzatq1;
    this.zzbrc = paramLong2;
    this.zzbrd = paramzzatq2;
    this.zzbre = paramLong3;
    this.zzbrf = paramzzatq3;
  }
  
  zzatg(zzatg paramzzatg)
  {
    this.versionCode = 1;
    zzac.zzw(paramzzatg);
    this.packageName = paramzzatg.packageName;
    this.zzbqW = paramzzatg.zzbqW;
    this.zzbqX = paramzzatg.zzbqX;
    this.zzbqY = paramzzatg.zzbqY;
    this.zzbqZ = paramzzatg.zzbqZ;
    this.zzbra = paramzzatg.zzbra;
    this.zzbrb = paramzzatg.zzbrb;
    this.zzbrc = paramzzatg.zzbrc;
    this.zzbrd = paramzzatg.zzbrd;
    this.zzbre = paramzzatg.zzbre;
    this.zzbrf = paramzzatg.zzbrf;
  }
  
  zzatg(String paramString1, String paramString2, zzauq paramzzauq, long paramLong1, boolean paramBoolean, String paramString3, zzatq paramzzatq1, long paramLong2, zzatq paramzzatq2, long paramLong3, zzatq paramzzatq3)
  {
    this.versionCode = 1;
    this.packageName = paramString1;
    this.zzbqW = paramString2;
    this.zzbqX = paramzzauq;
    this.zzbqY = paramLong1;
    this.zzbqZ = paramBoolean;
    this.zzbra = paramString3;
    this.zzbrb = paramzzatq1;
    this.zzbrc = paramLong2;
    this.zzbrd = paramzzatq2;
    this.zzbre = paramLong3;
    this.zzbrf = paramzzatq3;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzath.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */