package com.google.android.gms.internal.measurement;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.Iterator;

public final class zzer
  extends AbstractSafeParcelable
  implements Iterable<String>
{
  public static final Parcelable.Creator<zzer> CREATOR = new zzet();
  private final Bundle zzafw;
  
  zzer(Bundle paramBundle)
  {
    this.zzafw = paramBundle;
  }
  
  public final Iterator<String> iterator()
  {
    return new zzes(this);
  }
  
  public final String toString()
  {
    return this.zzafw.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBundle(paramParcel, 2, zzif(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final Bundle zzif()
  {
    return new Bundle(this.zzafw);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */