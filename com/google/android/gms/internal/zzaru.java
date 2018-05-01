package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzaru<M extends zzaru<M>>
  extends zzasa
{
  protected zzarw btG;
  
  public M cn()
    throws CloneNotSupportedException
  {
    zzaru localzzaru = (zzaru)super.co();
    zzary.zza(this, localzzaru);
    return localzzaru;
  }
  
  public final <T> T zza(zzarv<M, T> paramzzarv)
  {
    if (this.btG == null) {}
    zzarx localzzarx;
    do
    {
      return null;
      localzzarx = this.btG.zzahh(zzasd.zzahl(paramzzarv.tag));
    } while (localzzarx == null);
    return (T)localzzarx.zzb(paramzzarv);
  }
  
  public void zza(zzart paramzzart)
    throws IOException
  {
    if (this.btG == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.btG.size())
      {
        this.btG.zzahi(i).zza(paramzzart);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(zzars paramzzars, int paramInt)
    throws IOException
  {
    int i = paramzzars.getPosition();
    if (!paramzzars.zzagr(paramInt)) {
      return false;
    }
    int j = zzasd.zzahl(paramInt);
    zzasc localzzasc = new zzasc(paramInt, paramzzars.zzae(i, paramzzars.getPosition() - i));
    paramzzars = null;
    if (this.btG == null) {
      this.btG = new zzarw();
    }
    for (;;)
    {
      Object localObject = paramzzars;
      if (paramzzars == null)
      {
        localObject = new zzarx();
        this.btG.zza(j, (zzarx)localObject);
      }
      ((zzarx)localObject).zza(localzzasc);
      return true;
      paramzzars = this.btG.zzahh(j);
    }
  }
  
  protected int zzx()
  {
    int j = 0;
    if (this.btG != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.btG.size()) {
          break;
        }
        i += this.btG.zzahi(j).zzx();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaru.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */