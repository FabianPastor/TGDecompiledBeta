package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Intent;
import java.util.Arrays;

public final class zzah
{
  private final ComponentName mComponentName;
  private final String zzdrp;
  private final String zzgak;
  private final int zzgal;
  
  public zzah(String paramString1, String paramString2, int paramInt)
  {
    this.zzdrp = zzbq.zzgm(paramString1);
    this.zzgak = zzbq.zzgm(paramString2);
    this.mComponentName = null;
    this.zzgal = paramInt;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzah)) {
        return false;
      }
      paramObject = (zzah)paramObject;
    } while ((zzbg.equal(this.zzdrp, ((zzah)paramObject).zzdrp)) && (zzbg.equal(this.zzgak, ((zzah)paramObject).zzgak)) && (zzbg.equal(this.mComponentName, ((zzah)paramObject).mComponentName)) && (this.zzgal == ((zzah)paramObject).zzgal));
    return false;
  }
  
  public final ComponentName getComponentName()
  {
    return this.mComponentName;
  }
  
  public final String getPackage()
  {
    return this.zzgak;
  }
  
  public final int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.zzdrp, this.zzgak, this.mComponentName, Integer.valueOf(this.zzgal) });
  }
  
  public final String toString()
  {
    if (this.zzdrp == null) {
      return this.mComponentName.flattenToString();
    }
    return this.zzdrp;
  }
  
  public final int zzalk()
  {
    return this.zzgal;
  }
  
  public final Intent zzall()
  {
    if (this.zzdrp != null) {
      return new Intent(this.zzdrp).setPackage(this.zzgak);
    }
    return new Intent().setComponent(this.mComponentName);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzah.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */