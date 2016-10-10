package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzare<M extends zzare<M>>
  extends zzark
{
  protected zzarg bqv;
  
  public M cP()
    throws CloneNotSupportedException
  {
    zzare localzzare = (zzare)super.cQ();
    zzari.zza(this, localzzare);
    return localzzare;
  }
  
  public final <T> T zza(zzarf<M, T> paramzzarf)
  {
    if (this.bqv == null) {}
    zzarh localzzarh;
    do
    {
      return null;
      localzzarh = this.bqv.zzahq(zzarn.zzahu(paramzzarf.tag));
    } while (localzzarh == null);
    return (T)localzzarh.zzb(paramzzarf);
  }
  
  public void zza(zzard paramzzard)
    throws IOException
  {
    if (this.bqv == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.bqv.size())
      {
        this.bqv.zzahr(i).zza(paramzzard);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(zzarc paramzzarc, int paramInt)
    throws IOException
  {
    int i = paramzzarc.getPosition();
    if (!paramzzarc.zzaha(paramInt)) {
      return false;
    }
    int j = zzarn.zzahu(paramInt);
    zzarm localzzarm = new zzarm(paramInt, paramzzarc.zzad(i, paramzzarc.getPosition() - i));
    paramzzarc = null;
    if (this.bqv == null) {
      this.bqv = new zzarg();
    }
    for (;;)
    {
      Object localObject = paramzzarc;
      if (paramzzarc == null)
      {
        localObject = new zzarh();
        this.bqv.zza(j, (zzarh)localObject);
      }
      ((zzarh)localObject).zza(localzzarm);
      return true;
      paramzzarc = this.bqv.zzahq(j);
    }
  }
  
  protected int zzx()
  {
    int j = 0;
    if (this.bqv != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.bqv.size()) {
          break;
        }
        i += this.bqv.zzahr(j).zzx();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzare.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */