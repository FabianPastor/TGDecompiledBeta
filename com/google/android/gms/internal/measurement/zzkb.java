package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkb
  extends zzabd<zzkb>
{
  public Integer zzaru = null;
  public Boolean zzarv = null;
  public String zzarw = null;
  public String zzarx = null;
  public String zzary = null;
  
  public zzkb()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  private final zzkb zzd(zzaba paramzzaba)
    throws IOException
  {
    for (;;)
    {
      int i = paramzzaba.zzvo();
      switch (i)
      {
      default: 
        if (super.zza(paramzzaba, i)) {}
        break;
      case 0: 
        return this;
      case 8: 
        int j = paramzzaba.getPosition();
        int k;
        try
        {
          k = paramzzaba.zzvs();
          if ((k < 0) || (k > 4)) {
            break label126;
          }
          this.zzaru = Integer.valueOf(k);
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          paramzzaba.zzao(j);
          zza(paramzzaba, i);
        }
        continue;
        IllegalArgumentException localIllegalArgumentException2 = new java/lang/IllegalArgumentException;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(46);
        localIllegalArgumentException2.<init>(k + " is not a valid enum ComparisonType");
        throw localIllegalArgumentException2;
      case 16: 
        this.zzarv = Boolean.valueOf(paramzzaba.zzvr());
        break;
      case 26: 
        this.zzarw = paramzzaba.readString();
        break;
      case 34: 
        this.zzarx = paramzzaba.readString();
        break;
      case 42: 
        label126:
        this.zzary = paramzzaba.readString();
      }
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramObject == this) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if (!(paramObject instanceof zzkb))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkb)paramObject;
        if (this.zzaru == null)
        {
          if (((zzkb)paramObject).zzaru != null) {
            bool2 = false;
          }
        }
        else if (!this.zzaru.equals(((zzkb)paramObject).zzaru))
        {
          bool2 = false;
          continue;
        }
        if (this.zzarv == null)
        {
          if (((zzkb)paramObject).zzarv != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarv.equals(((zzkb)paramObject).zzarv))
        {
          bool2 = false;
          continue;
        }
        if (this.zzarw == null)
        {
          if (((zzkb)paramObject).zzarw != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarw.equals(((zzkb)paramObject).zzarw))
        {
          bool2 = false;
          continue;
        }
        if (this.zzarx == null)
        {
          if (((zzkb)paramObject).zzarx != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarx.equals(((zzkb)paramObject).zzarx))
        {
          bool2 = false;
          continue;
        }
        if (this.zzary == null)
        {
          if (((zzkb)paramObject).zzary != null) {
            bool2 = false;
          }
        }
        else if (!this.zzary.equals(((zzkb)paramObject).zzary))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkb)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkb)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkb)paramObject).zzbzh);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    int k;
    int m;
    label32:
    int n;
    label42:
    int i1;
    label52:
    int i2;
    if (this.zzaru == null)
    {
      k = 0;
      if (this.zzarv != null) {
        break label137;
      }
      m = 0;
      if (this.zzarw != null) {
        break label149;
      }
      n = 0;
      if (this.zzarx != null) {
        break label161;
      }
      i1 = 0;
      if (this.zzary != null) {
        break label173;
      }
      i2 = 0;
      label62:
      i3 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label185;
        }
      }
    }
    label137:
    label149:
    label161:
    label173:
    label185:
    for (int i3 = i;; i3 = this.zzbzh.hashCode())
    {
      return (i2 + (i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i3;
      k = this.zzaru.intValue();
      break;
      m = this.zzarv.hashCode();
      break label32;
      n = this.zzarw.hashCode();
      break label42;
      i1 = this.zzarx.hashCode();
      break label52;
      i2 = this.zzary.hashCode();
      break label62;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzaru != null) {
      j = i + zzabb.zzf(1, this.zzaru.intValue());
    }
    i = j;
    if (this.zzarv != null)
    {
      this.zzarv.booleanValue();
      i = j + (zzabb.zzas(2) + 1);
    }
    j = i;
    if (this.zzarw != null) {
      j = i + zzabb.zzd(3, this.zzarw);
    }
    i = j;
    if (this.zzarx != null) {
      i = j + zzabb.zzd(4, this.zzarx);
    }
    j = i;
    if (this.zzary != null) {
      j = i + zzabb.zzd(5, this.zzary);
    }
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzaru != null) {
      paramzzabb.zze(1, this.zzaru.intValue());
    }
    if (this.zzarv != null) {
      paramzzabb.zza(2, this.zzarv.booleanValue());
    }
    if (this.zzarw != null) {
      paramzzabb.zzc(3, this.zzarw);
    }
    if (this.zzarx != null) {
      paramzzabb.zzc(4, this.zzarx);
    }
    if (this.zzary != null) {
      paramzzabb.zzc(5, this.zzary);
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */