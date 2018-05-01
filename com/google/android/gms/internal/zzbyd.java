package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyd<M extends zzbyd<M>>
  extends zzbyj
{
  protected zzbyf zzcwC;
  
  private void zza(int paramInt, zzbyl paramzzbyl)
  {
    zzbyg localzzbyg1 = null;
    if (this.zzcwC == null) {
      this.zzcwC = new zzbyf();
    }
    for (;;)
    {
      zzbyg localzzbyg2 = localzzbyg1;
      if (localzzbyg1 == null)
      {
        localzzbyg2 = new zzbyg();
        this.zzcwC.zza(paramInt, localzzbyg2);
      }
      localzzbyg2.zza(paramzzbyl);
      return;
      localzzbyg1 = this.zzcwC.zzrt(paramInt);
    }
  }
  
  public final <T> T zza(zzbye<M, T> paramzzbye)
  {
    if (this.zzcwC == null) {}
    zzbyg localzzbyg;
    do
    {
      return null;
      localzzbyg = this.zzcwC.zzrt(zzbym.zzrx(paramzzbye.tag));
    } while (localzzbyg == null);
    return (T)localzzbyg.zzb(paramzzbye);
  }
  
  public void zza(zzbyc paramzzbyc)
    throws IOException
  {
    if (this.zzcwC == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.zzcwC.size())
      {
        this.zzcwC.zzru(i).zza(paramzzbyc);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(zzbyb paramzzbyb, int paramInt)
    throws IOException
  {
    int i = paramzzbyb.getPosition();
    if (!paramzzbyb.zzrd(paramInt)) {
      return false;
    }
    zza(zzbym.zzrx(paramInt), new zzbyl(paramInt, paramzzbyb.zzI(i, paramzzbyb.getPosition() - i)));
    return true;
  }
  
  public M zzafp()
    throws CloneNotSupportedException
  {
    zzbyd localzzbyd = (zzbyd)super.zzafq();
    zzbyh.zza(this, localzzbyd);
    return localzzbyd;
  }
  
  protected int zzu()
  {
    int j = 0;
    if (this.zzcwC != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.zzcwC.size()) {
          break;
        }
        i += this.zzcwC.zzru(j).zzu();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */