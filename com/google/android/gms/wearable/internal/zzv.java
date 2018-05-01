package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.CapabilityInfo;

final class zzv
  implements CapabilityApi.CapabilityListener
{
  private CapabilityApi.CapabilityListener zzbRY;
  private String zzbRZ;
  
  zzv(CapabilityApi.CapabilityListener paramCapabilityListener, String paramString)
  {
    this.zzbRY = paramCapabilityListener;
    this.zzbRZ = paramString;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1;
    if (this == paramObject) {
      bool1 = true;
    }
    do
    {
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (paramObject == null);
        bool1 = bool2;
      } while (getClass() != paramObject.getClass());
      paramObject = (zzv)paramObject;
      bool1 = bool2;
    } while (!this.zzbRY.equals(((zzv)paramObject).zzbRY));
    return this.zzbRZ.equals(((zzv)paramObject).zzbRZ);
  }
  
  public final int hashCode()
  {
    return this.zzbRY.hashCode() * 31 + this.zzbRZ.hashCode();
  }
  
  public final void onCapabilityChanged(CapabilityInfo paramCapabilityInfo)
  {
    this.zzbRY.onCapabilityChanged(paramCapabilityInfo);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */