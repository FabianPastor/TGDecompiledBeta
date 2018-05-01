package com.google.android.gms.internal.config;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzaj
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzaj> CREATOR = new zzak();
  private final Bundle zzae;
  
  public zzaj(Bundle paramBundle)
  {
    this.zzae = paramBundle;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBundle(paramParcel, 2, this.zzae, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final Bundle zzm()
  {
    return this.zzae;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */