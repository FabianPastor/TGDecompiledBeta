package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjl
  extends zza
{
  public static final Parcelable.Creator<zzbjl> CREATOR = new zzbjm();
  final int versionCode;
  public int zzbOJ;
  
  public zzbjl()
  {
    this.versionCode = 1;
  }
  
  public zzbjl(int paramInt1, int paramInt2)
  {
    this.versionCode = paramInt1;
    this.zzbOJ = paramInt2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbjm.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */