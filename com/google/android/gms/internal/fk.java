package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fk
  extends zza
{
  public static final Parcelable.Creator<fk> CREATOR = new fl();
  public final String zzbNS;
  public final ft[] zzbNX;
  public final fe zzbNY;
  private fe zzbNZ;
  private fe zzbOa;
  public final String zzbOb;
  private float zzbOc;
  private int zzbOd;
  public final boolean zzbOe;
  public final int zzbOf;
  public final int zzbOg;
  
  public fk(ft[] paramArrayOfft, fe paramfe1, fe paramfe2, fe paramfe3, String paramString1, float paramFloat, String paramString2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    this.zzbNX = paramArrayOfft;
    this.zzbNY = paramfe1;
    this.zzbNZ = paramfe2;
    this.zzbOa = paramfe3;
    this.zzbOb = paramString1;
    this.zzbOc = paramFloat;
    this.zzbNS = paramString2;
    this.zzbOd = paramInt1;
    this.zzbOe = paramBoolean;
    this.zzbOf = paramInt2;
    this.zzbOg = paramInt3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbNX, paramInt, false);
    zzd.zza(paramParcel, 3, this.zzbNY, paramInt, false);
    zzd.zza(paramParcel, 4, this.zzbNZ, paramInt, false);
    zzd.zza(paramParcel, 5, this.zzbOa, paramInt, false);
    zzd.zza(paramParcel, 6, this.zzbOb, false);
    zzd.zza(paramParcel, 7, this.zzbOc);
    zzd.zza(paramParcel, 8, this.zzbNS, false);
    zzd.zzc(paramParcel, 9, this.zzbOd);
    zzd.zza(paramParcel, 10, this.zzbOe);
    zzd.zzc(paramParcel, 11, this.zzbOf);
    zzd.zzc(paramParcel, 12, this.zzbOg);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */