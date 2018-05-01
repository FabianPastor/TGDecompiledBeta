package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzbjx
  extends zza
{
  public static final Parcelable.Creator<zzbjx> CREATOR = new zzbjy();
  public final int type;
  public final int versionCode;
  public final float x;
  public final float y;
  
  public zzbjx(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2)
  {
    this.versionCode = paramInt1;
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.type = paramInt2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbjy.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */