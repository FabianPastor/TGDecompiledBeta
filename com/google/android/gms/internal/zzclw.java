package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclw
  extends zzfjm<zzclw>
{
  public Integer zzjko = null;
  public String zzjkp = null;
  public Boolean zzjkq = null;
  public String[] zzjkr = zzfjv.EMPTY_STRING_ARRAY;
  
  public zzclw()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  private final zzclw zzi(zzfjj paramzzfjj)
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
          throw new IllegalArgumentException(41 + k + " is not a valid enum MatchType");
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        paramzzfjj.zzmg(j);
        zza(paramzzfjj, i);
      }
      break;
      this.zzjko = Integer.valueOf(k);
      break;
      this.zzjkp = paramzzfjj.readString();
      break;
      this.zzjkq = Boolean.valueOf(paramzzfjj.zzcvz());
      break;
      j = zzfjv.zzb(paramzzfjj, 34);
      if (this.zzjkr == null) {}
      String[] arrayOfString;
      for (i = 0;; i = this.zzjkr.length)
      {
        arrayOfString = new String[j + i];
        j = i;
        if (i != 0)
        {
          System.arraycopy(this.zzjkr, 0, arrayOfString, 0, i);
          j = i;
        }
        while (j < arrayOfString.length - 1)
        {
          arrayOfString[j] = paramzzfjj.readString();
          paramzzfjj.zzcvt();
          j += 1;
        }
      }
      arrayOfString[j] = paramzzfjj.readString();
      this.zzjkr = arrayOfString;
      break;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclw)) {
        return false;
      }
      paramObject = (zzclw)paramObject;
      if (this.zzjko == null)
      {
        if (((zzclw)paramObject).zzjko != null) {
          return false;
        }
      }
      else if (!this.zzjko.equals(((zzclw)paramObject).zzjko)) {
        return false;
      }
      if (this.zzjkp == null)
      {
        if (((zzclw)paramObject).zzjkp != null) {
          return false;
        }
      }
      else if (!this.zzjkp.equals(((zzclw)paramObject).zzjkp)) {
        return false;
      }
      if (this.zzjkq == null)
      {
        if (((zzclw)paramObject).zzjkq != null) {
          return false;
        }
      }
      else if (!this.zzjkq.equals(((zzclw)paramObject).zzjkq)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjkr, ((zzclw)paramObject).zzjkr)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclw)paramObject).zzpnc == null) || (((zzclw)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclw)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int n = 0;
    int i1 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int k;
    label42:
    int i2;
    if (this.zzjko == null)
    {
      i = 0;
      if (this.zzjkp != null) {
        break label121;
      }
      j = 0;
      if (this.zzjkq != null) {
        break label132;
      }
      k = 0;
      i2 = zzfjq.hashCode(this.zzjkr);
      m = n;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label143;
        }
      }
    }
    label121:
    label132:
    label143:
    for (int m = n;; m = this.zzpnc.hashCode())
    {
      return ((k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + i2) * 31 + m;
      i = this.zzjko.intValue();
      break;
      j = this.zzjkp.hashCode();
      break label33;
      k = this.zzjkq.hashCode();
      break label42;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjko != null) {
      paramzzfjk.zzaa(1, this.zzjko.intValue());
    }
    if (this.zzjkp != null) {
      paramzzfjk.zzn(2, this.zzjkp);
    }
    if (this.zzjkq != null) {
      paramzzfjk.zzl(3, this.zzjkq.booleanValue());
    }
    if ((this.zzjkr != null) && (this.zzjkr.length > 0))
    {
      int i = 0;
      while (i < this.zzjkr.length)
      {
        String str = this.zzjkr[i];
        if (str != null) {
          paramzzfjk.zzn(4, str);
        }
        i += 1;
      }
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int n = 0;
    int j = super.zzq();
    int i = j;
    if (this.zzjko != null) {
      i = j + zzfjk.zzad(1, this.zzjko.intValue());
    }
    j = i;
    if (this.zzjkp != null) {
      j = i + zzfjk.zzo(2, this.zzjkp);
    }
    i = j;
    if (this.zzjkq != null)
    {
      this.zzjkq.booleanValue();
      i = j + (zzfjk.zzlg(3) + 1);
    }
    j = i;
    if (this.zzjkr != null)
    {
      j = i;
      if (this.zzjkr.length > 0)
      {
        int k = 0;
        int m = 0;
        j = n;
        while (j < this.zzjkr.length)
        {
          String str = this.zzjkr[j];
          int i1 = k;
          n = m;
          if (str != null)
          {
            n = m + 1;
            i1 = k + zzfjk.zztt(str);
          }
          j += 1;
          k = i1;
          m = n;
        }
        j = i + k + m * 1;
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */