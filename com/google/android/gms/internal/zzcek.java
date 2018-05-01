package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzcek
  extends zza
{
  public static final Parcelable.Creator<zzcek> CREATOR = new zzcel();
  public String packageName;
  private int versionCode;
  public String zzbpc;
  public zzcji zzbpd;
  public long zzbpe;
  public boolean zzbpf;
  public String zzbpg;
  public zzcez zzbph;
  public long zzbpi;
  public zzcez zzbpj;
  public long zzbpk;
  public zzcez zzbpl;
  
  zzcek(int paramInt, String paramString1, String paramString2, zzcji paramzzcji, long paramLong1, boolean paramBoolean, String paramString3, zzcez paramzzcez1, long paramLong2, zzcez paramzzcez2, long paramLong3, zzcez paramzzcez3)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.zzbpc = paramString2;
    this.zzbpd = paramzzcji;
    this.zzbpe = paramLong1;
    this.zzbpf = paramBoolean;
    this.zzbpg = paramString3;
    this.zzbph = paramzzcez1;
    this.zzbpi = paramLong2;
    this.zzbpj = paramzzcez2;
    this.zzbpk = paramLong3;
    this.zzbpl = paramzzcez3;
  }
  
  zzcek(zzcek paramzzcek)
  {
    this.versionCode = 1;
    zzbo.zzu(paramzzcek);
    this.packageName = paramzzcek.packageName;
    this.zzbpc = paramzzcek.zzbpc;
    this.zzbpd = paramzzcek.zzbpd;
    this.zzbpe = paramzzcek.zzbpe;
    this.zzbpf = paramzzcek.zzbpf;
    this.zzbpg = paramzzcek.zzbpg;
    this.zzbph = paramzzcek.zzbph;
    this.zzbpi = paramzzcek.zzbpi;
    this.zzbpj = paramzzcek.zzbpj;
    this.zzbpk = paramzzcek.zzbpk;
    this.zzbpl = paramzzcek.zzbpl;
  }
  
  zzcek(String paramString1, String paramString2, zzcji paramzzcji, long paramLong1, boolean paramBoolean, String paramString3, zzcez paramzzcez1, long paramLong2, zzcez paramzzcez2, long paramLong3, zzcez paramzzcez3)
  {
    this.versionCode = 1;
    this.packageName = paramString1;
    this.zzbpc = paramString2;
    this.zzbpd = paramzzcji;
    this.zzbpe = paramLong1;
    this.zzbpf = paramBoolean;
    this.zzbpg = paramString3;
    this.zzbph = paramzzcez1;
    this.zzbpi = paramLong2;
    this.zzbpj = paramzzcez2;
    this.zzbpk = paramLong3;
    this.zzbpl = paramzzcez3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.versionCode);
    zzd.zza(paramParcel, 2, this.packageName, false);
    zzd.zza(paramParcel, 3, this.zzbpc, false);
    zzd.zza(paramParcel, 4, this.zzbpd, paramInt, false);
    zzd.zza(paramParcel, 5, this.zzbpe);
    zzd.zza(paramParcel, 6, this.zzbpf);
    zzd.zza(paramParcel, 7, this.zzbpg, false);
    zzd.zza(paramParcel, 8, this.zzbph, paramInt, false);
    zzd.zza(paramParcel, 9, this.zzbpi);
    zzd.zza(paramParcel, 10, this.zzbpj, paramInt, false);
    zzd.zza(paramParcel, 11, this.zzbpk);
    zzd.zza(paramParcel, 12, this.zzbpl, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcek.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */