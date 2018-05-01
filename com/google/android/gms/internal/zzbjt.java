package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjt
  extends zza
{
  public static final Parcelable.Creator<zzbjt> CREATOR = new zzbju();
  public int mode;
  public final int versionCode;
  public int zzbPh;
  public int zzbPi;
  public boolean zzbPj;
  public boolean zzbPk;
  public float zzbPl;
  
  public zzbjt()
  {
    this.versionCode = 2;
  }
  
  public zzbjt(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, float paramFloat)
  {
    this.versionCode = paramInt1;
    this.mode = paramInt2;
    this.zzbPh = paramInt3;
    this.zzbPi = paramInt4;
    this.zzbPj = paramBoolean1;
    this.zzbPk = paramBoolean2;
    this.zzbPl = paramFloat;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbju.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */