package com.google.android.gms.maps;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class CameraUpdate
{
  private final IObjectWrapper zze;
  
  CameraUpdate(IObjectWrapper paramIObjectWrapper)
  {
    this.zze = ((IObjectWrapper)Preconditions.checkNotNull(paramIObjectWrapper));
  }
  
  public final IObjectWrapper zza()
  {
    return this.zze;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/CameraUpdate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */