package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fm
  extends zza
{
  public static final Parcelable.Creator<fm> CREATOR = new fn();
  public final Rect zzbOh;
  
  public fm()
  {
    this.zzbOh = new Rect();
  }
  
  public fm(Rect paramRect)
  {
    this.zzbOh = paramRect;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbOh, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */