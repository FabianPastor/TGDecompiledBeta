package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzct
  extends zza
{
  public static final Parcelable.Creator<zzct> CREATOR = new zzcs();
  private boolean enabled;
  private int statusCode;
  
  public zzct(int paramInt, boolean paramBoolean)
  {
    this.statusCode = paramInt;
    this.enabled = paramBoolean;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.statusCode);
    zzd.zza(paramParcel, 3, this.enabled);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzct.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */