package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class ft
  extends zza
{
  public static final Parcelable.Creator<ft> CREATOR = new fu();
  public final String zzbNS;
  public final fe zzbNY;
  private fe zzbNZ;
  public final String zzbOb;
  private float zzbOc;
  private fo[] zzbOi;
  private boolean zzbOj;
  
  public ft(fo[] paramArrayOffo, fe paramfe1, fe paramfe2, String paramString1, float paramFloat, String paramString2, boolean paramBoolean)
  {
    this.zzbOi = paramArrayOffo;
    this.zzbNY = paramfe1;
    this.zzbNZ = paramfe2;
    this.zzbOb = paramString1;
    this.zzbOc = paramFloat;
    this.zzbNS = paramString2;
    this.zzbOj = paramBoolean;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbOi, paramInt, false);
    zzd.zza(paramParcel, 3, this.zzbNY, paramInt, false);
    zzd.zza(paramParcel, 4, this.zzbNZ, paramInt, false);
    zzd.zza(paramParcel, 5, this.zzbOb, false);
    zzd.zza(paramParcel, 6, this.zzbOc);
    zzd.zza(paramParcel, 7, this.zzbNS, false);
    zzd.zza(paramParcel, 8, this.zzbOj);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ft.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */