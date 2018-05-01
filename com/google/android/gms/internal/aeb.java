package com.google.android.gms.internal;

import java.io.IOException;

public final class aeb
  extends ada<aeb>
  implements Cloneable
{
  private static volatile aeb[] zzctU;
  private String key = "";
  private String value = "";
  
  public aeb()
  {
    this.zzcrZ = null;
    this.zzcsi = -1;
  }
  
  public static aeb[] zzMc()
  {
    if (zzctU == null) {}
    synchronized (ade.zzcsh)
    {
      if (zzctU == null) {
        zzctU = new aeb[0];
      }
      return zzctU;
    }
  }
  
  private aeb zzMd()
  {
    try
    {
      aeb localaeb = (aeb)super.zzLL();
      return localaeb;
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
      if (!(paramObject instanceof aeb)) {
        return false;
      }
      paramObject = (aeb)paramObject;
      if (this.key == null)
      {
        if (((aeb)paramObject).key != null) {
          return false;
        }
      }
      else if (!this.key.equals(((aeb)paramObject).key)) {
        return false;
      }
      if (this.value == null)
      {
        if (((aeb)paramObject).value != null) {
          return false;
        }
      }
      else if (!this.value.equals(((aeb)paramObject).value)) {
        return false;
      }
      if ((this.zzcrZ != null) && (!this.zzcrZ.isEmpty())) {
        break;
      }
    } while ((((aeb)paramObject).zzcrZ == null) || (((aeb)paramObject).zzcrZ.isEmpty()));
    return false;
    return this.zzcrZ.equals(((aeb)paramObject).zzcrZ);
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
      if (this.zzcrZ != null) {
        if (!this.zzcrZ.isEmpty()) {
          break label100;
        }
      }
    }
    label89:
    label100:
    for (int k = m;; k = this.zzcrZ.hashCode())
    {
      return (j + (i + (n + 527) * 31) * 31) * 31 + k;
      i = this.key.hashCode();
      break;
      j = this.value.hashCode();
      break label33;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    if ((this.key != null) && (!this.key.equals(""))) {
      paramacy.zzl(1, this.key);
    }
    if ((this.value != null) && (!this.value.equals(""))) {
      paramacy.zzl(2, this.value);
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.key != null)
    {
      i = j;
      if (!this.key.equals("")) {
        i = j + acy.zzm(1, this.key);
      }
    }
    j = i;
    if (this.value != null)
    {
      j = i;
      if (!this.value.equals("")) {
        j = i + acy.zzm(2, this.value);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aeb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */