package com.google.android.gms.internal;

import java.io.IOException;

public final class aek
  extends adj<aek>
  implements Cloneable
{
  private static volatile aek[] zzcuj;
  private String key = "";
  private String value = "";
  
  public aek()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  public static aek[] zzMe()
  {
    if (zzcuj == null) {}
    synchronized (adn.zzcsw)
    {
      if (zzcuj == null) {
        zzcuj = new aek[0];
      }
      return zzcuj;
    }
  }
  
  private aek zzMf()
  {
    try
    {
      aek localaek = (aek)super.zzLN();
      return localaek;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof aek)) {
        return false;
      }
      paramObject = (aek)paramObject;
      if (this.key == null)
      {
        if (((aek)paramObject).key != null) {
          return false;
        }
      }
      else if (!this.key.equals(((aek)paramObject).key)) {
        return false;
      }
      if (this.value == null)
      {
        if (((aek)paramObject).value != null) {
          return false;
        }
      }
      else if (!this.value.equals(((aek)paramObject).value)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((aek)paramObject).zzcso == null) || (((aek)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((aek)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i;
    int j;
    if (this.key == null)
    {
      i = 0;
      if (this.value != null) {
        break label89;
      }
      j = 0;
      label33:
      k = m;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label100;
        }
      }
    }
    label89:
    label100:
    for (int k = m;; k = this.zzcso.hashCode())
    {
      return (j + (i + (n + 527) * 31) * 31) * 31 + k;
      i = this.key.hashCode();
      break;
      j = this.value.hashCode();
      break label33;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if ((this.key != null) && (!this.key.equals(""))) {
      paramadh.zzl(1, this.key);
    }
    if ((this.value != null) && (!this.value.equals(""))) {
      paramadh.zzl(2, this.value);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.key != null)
    {
      i = j;
      if (!this.key.equals("")) {
        i = j + adh.zzm(1, this.key);
      }
    }
    j = i;
    if (this.value != null)
    {
      j = i;
      if (!this.value.equals("")) {
        j = i + adh.zzm(2, this.value);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aek.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */