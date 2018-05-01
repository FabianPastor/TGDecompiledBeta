package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclu
  extends zzfjm<zzclu>
{
  public Integer zzjkg = null;
  public Boolean zzjkh = null;
  public String zzjki = null;
  public String zzjkj = null;
  public String zzjkk = null;
  
  public zzclu()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  private final zzclu zzh(zzfjj paramzzfjj)
    throws IOException
  {
    int i;
    do
    {
      i = paramzzfjj.zzcvt();
      switch (i)
      {
      }
    } while (super.zza(paramzzfjj, i));
    return this;
    int j = paramzzfjj.getPosition();
    for (;;)
    {
      int k;
      try
      {
        k = paramzzfjj.zzcwi();
        switch (k)
        {
        case 0: 
          throw new IllegalArgumentException(46 + k + " is not a valid enum ComparisonType");
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        paramzzfjj.zzmg(j);
        zza(paramzzfjj, i);
      }
      break;
      this.zzjkg = Integer.valueOf(k);
      break;
      this.zzjkh = Boolean.valueOf(paramzzfjj.zzcvz());
      break;
      this.zzjki = paramzzfjj.readString();
      break;
      this.zzjkj = paramzzfjj.readString();
      break;
      this.zzjkk = paramzzfjj.readString();
      break;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclu)) {
        return false;
      }
      paramObject = (zzclu)paramObject;
      if (this.zzjkg == null)
      {
        if (((zzclu)paramObject).zzjkg != null) {
          return false;
        }
      }
      else if (!this.zzjkg.equals(((zzclu)paramObject).zzjkg)) {
        return false;
      }
      if (this.zzjkh == null)
      {
        if (((zzclu)paramObject).zzjkh != null) {
          return false;
        }
      }
      else if (!this.zzjkh.equals(((zzclu)paramObject).zzjkh)) {
        return false;
      }
      if (this.zzjki == null)
      {
        if (((zzclu)paramObject).zzjki != null) {
          return false;
        }
      }
      else if (!this.zzjki.equals(((zzclu)paramObject).zzjki)) {
        return false;
      }
      if (this.zzjkj == null)
      {
        if (((zzclu)paramObject).zzjkj != null) {
          return false;
        }
      }
      else if (!this.zzjkj.equals(((zzclu)paramObject).zzjkj)) {
        return false;
      }
      if (this.zzjkk == null)
      {
        if (((zzclu)paramObject).zzjkk != null) {
          return false;
        }
      }
      else if (!this.zzjkk.equals(((zzclu)paramObject).zzjkk)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclu)paramObject).zzpnc == null) || (((zzclu)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclu)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i2 = 0;
    int i3 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int k;
    label42:
    int m;
    label52:
    int n;
    if (this.zzjkg == null)
    {
      i = 0;
      if (this.zzjkh != null) {
        break label138;
      }
      j = 0;
      if (this.zzjki != null) {
        break label149;
      }
      k = 0;
      if (this.zzjkj != null) {
        break label160;
      }
      m = 0;
      if (this.zzjkk != null) {
        break label172;
      }
      n = 0;
      label62:
      i1 = i2;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label184;
        }
      }
    }
    label138:
    label149:
    label160:
    label172:
    label184:
    for (int i1 = i2;; i1 = this.zzpnc.hashCode())
    {
      return (n + (m + (k + (j + (i + (i3 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
      i = this.zzjkg.intValue();
      break;
      j = this.zzjkh.hashCode();
      break label33;
      k = this.zzjki.hashCode();
      break label42;
      m = this.zzjkj.hashCode();
      break label52;
      n = this.zzjkk.hashCode();
      break label62;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjkg != null) {
      paramzzfjk.zzaa(1, this.zzjkg.intValue());
    }
    if (this.zzjkh != null) {
      paramzzfjk.zzl(2, this.zzjkh.booleanValue());
    }
    if (this.zzjki != null) {
      paramzzfjk.zzn(3, this.zzjki);
    }
    if (this.zzjkj != null) {
      paramzzfjk.zzn(4, this.zzjkj);
    }
    if (this.zzjkk != null) {
      paramzzfjk.zzn(5, this.zzjkk);
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.zzjkg != null) {
      i = j + zzfjk.zzad(1, this.zzjkg.intValue());
    }
    j = i;
    if (this.zzjkh != null)
    {
      this.zzjkh.booleanValue();
      j = i + (zzfjk.zzlg(2) + 1);
    }
    i = j;
    if (this.zzjki != null) {
      i = j + zzfjk.zzo(3, this.zzjki);
    }
    j = i;
    if (this.zzjkj != null) {
      j = i + zzfjk.zzo(4, this.zzjkj);
    }
    i = j;
    if (this.zzjkk != null) {
      i = j + zzfjk.zzo(5, this.zzjkk);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */