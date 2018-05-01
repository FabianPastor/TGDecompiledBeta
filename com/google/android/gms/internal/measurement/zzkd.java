package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkd
  extends zzabd<zzkd>
{
  public Integer zzasc = null;
  public String zzasd = null;
  public Boolean zzase = null;
  public String[] zzasf = zzabm.zzcac;
  
  public zzkd()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  private final zzkd zze(zzaba paramzzaba)
    throws IOException
  {
    for (;;)
    {
      int i = paramzzaba.zzvo();
      int j;
      int k;
      label119:
      Object localObject;
      switch (i)
      {
      default: 
        if (super.zza(paramzzaba, i)) {}
        break;
      case 0: 
        return this;
      case 8: 
        j = paramzzaba.getPosition();
        try
        {
          k = paramzzaba.zzvs();
          if ((k < 0) || (k > 6)) {
            break label119;
          }
          this.zzasc = Integer.valueOf(k);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          paramzzaba.zzao(j);
          zza(paramzzaba, i);
        }
        continue;
        localObject = new java/lang/IllegalArgumentException;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(41);
        ((IllegalArgumentException)localObject).<init>(k + " is not a valid enum MatchType");
        throw ((Throwable)localObject);
      case 18: 
        this.zzasd = paramzzaba.readString();
        break;
      case 24: 
        this.zzase = Boolean.valueOf(paramzzaba.zzvr());
        break;
      case 34: 
        j = zzabm.zzb(paramzzaba, 34);
        if (this.zzasf == null) {}
        for (k = 0;; k = this.zzasf.length)
        {
          localObject = new String[j + k];
          j = k;
          if (k != 0) {
            System.arraycopy(this.zzasf, 0, localObject, 0, k);
          }
          for (j = k; j < localObject.length - 1; j++)
          {
            localObject[j] = paramzzaba.readString();
            paramzzaba.zzvo();
          }
        }
        localObject[j] = paramzzaba.readString();
        this.zzasf = ((String[])localObject);
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
      if (!(paramObject instanceof zzkd))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkd)paramObject;
        if (this.zzasc == null)
        {
          if (((zzkd)paramObject).zzasc != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasc.equals(((zzkd)paramObject).zzasc))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasd == null)
        {
          if (((zzkd)paramObject).zzasd != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasd.equals(((zzkd)paramObject).zzasd))
        {
          bool2 = false;
          continue;
        }
        if (this.zzase == null)
        {
          if (((zzkd)paramObject).zzase != null) {
            bool2 = false;
          }
        }
        else if (!this.zzase.equals(((zzkd)paramObject).zzase))
        {
          bool2 = false;
          continue;
        }
        if (!zzabh.equals(this.zzasf, ((zzkd)paramObject).zzasf))
        {
          bool2 = false;
        }
        else if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkd)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkd)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkd)paramObject).zzbzh);
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
    if (this.zzasc == null)
    {
      k = 0;
      if (this.zzasd != null) {
        break label120;
      }
      m = 0;
      if (this.zzase != null) {
        break label132;
      }
      n = 0;
      i1 = zzabh.hashCode(this.zzasf);
      i2 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label144;
        }
      }
    }
    label120:
    label132:
    label144:
    for (int i2 = i;; i2 = this.zzbzh.hashCode())
    {
      return ((n + (m + (k + (j + 527) * 31) * 31) * 31) * 31 + i1) * 31 + i2;
      k = this.zzasc.intValue();
      break;
      m = this.zzasd.hashCode();
      break label32;
      n = this.zzase.hashCode();
      break label42;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzasc != null) {
      j = i + zzabb.zzf(1, this.zzasc.intValue());
    }
    i = j;
    if (this.zzasd != null) {
      i = j + zzabb.zzd(2, this.zzasd);
    }
    j = i;
    if (this.zzase != null)
    {
      this.zzase.booleanValue();
      j = i + (zzabb.zzas(3) + 1);
    }
    i = j;
    int k;
    int m;
    if (this.zzasf != null)
    {
      i = j;
      if (this.zzasf.length > 0)
      {
        k = 0;
        i = 0;
        m = 0;
        if (k < this.zzasf.length)
        {
          String str = this.zzasf[k];
          if (str == null) {
            break label150;
          }
          m++;
          i = zzabb.zzfp(str) + i;
        }
      }
    }
    label150:
    for (;;)
    {
      k++;
      break;
      i = j + i + m * 1;
      return i;
    }
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzasc != null) {
      paramzzabb.zze(1, this.zzasc.intValue());
    }
    if (this.zzasd != null) {
      paramzzabb.zzc(2, this.zzasd);
    }
    if (this.zzase != null) {
      paramzzabb.zza(3, this.zzase.booleanValue());
    }
    if ((this.zzasf != null) && (this.zzasf.length > 0)) {
      for (int i = 0; i < this.zzasf.length; i++)
      {
        String str = this.zzasf[i];
        if (str != null) {
          paramzzabb.zzc(4, str);
        }
      }
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */