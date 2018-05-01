package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adj<M extends adj<M>>
  extends adp
{
  protected adl zzcso;
  
  public M zzLN()
    throws CloneNotSupportedException
  {
    adj localadj = (adj)super.zzLO();
    adn.zza(this, localadj);
    return localadj;
  }
  
  public final <T> T zza(adk<M, T> paramadk)
  {
    if (this.zzcso == null) {}
    adm localadm;
    do
    {
      return null;
      localadm = this.zzcso.zzcx(paramadk.tag >>> 3);
    } while (localadm == null);
    return (T)localadm.zzb(paramadk);
  }
  
  public void zza(adh paramadh)
    throws IOException
  {
    if (this.zzcso == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.zzcso.size())
      {
        this.zzcso.zzcy(i).zza(paramadh);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(adg paramadg, int paramInt)
    throws IOException
  {
    int i = paramadg.getPosition();
    if (!paramadg.zzcm(paramInt)) {
      return false;
    }
    int j = paramInt >>> 3;
    adr localadr = new adr(paramInt, paramadg.zzp(i, paramadg.getPosition() - i));
    paramadg = null;
    if (this.zzcso == null) {
      this.zzcso = new adl();
    }
    for (;;)
    {
      Object localObject = paramadg;
      if (paramadg == null)
      {
        localObject = new adm();
        this.zzcso.zza(j, (adm)localObject);
      }
      ((adm)localObject).zza(localadr);
      return true;
      paramadg = this.zzcso.zzcx(j);
    }
  }
  
  protected int zzn()
  {
    int j = 0;
    if (this.zzcso != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.zzcso.size()) {
          break;
        }
        i += this.zzcso.zzcy(j).zzn();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */