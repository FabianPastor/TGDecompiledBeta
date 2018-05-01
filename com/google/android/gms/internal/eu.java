package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class eu
  extends zza
{
  public static final Parcelable.Creator<eu> CREATOR = new ev();
  public int zzbNi;
  
  public eu() {}
  
  public eu(int paramInt)
  {
    this.zzbNi = paramInt;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.zzbNi);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/eu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */