package com.google.android.gms.internal;

import java.io.IOException;

final class zzaos<T>
  extends zzaot<T>
{
  private zzaot<T> bkU;
  private final zzaop<T> blj;
  private final zzaog<T> blk;
  private final zzaob bll;
  private final zzapx<T> blm;
  private final zzaou bln;
  
  private zzaos(zzaop<T> paramzzaop, zzaog<T> paramzzaog, zzaob paramzzaob, zzapx<T> paramzzapx, zzaou paramzzaou)
  {
    this.blj = paramzzaop;
    this.blk = paramzzaog;
    this.bll = paramzzaob;
    this.blm = paramzzapx;
    this.bln = paramzzaou;
  }
  
  private zzaot<T> bd()
  {
    zzaot localzzaot = this.bkU;
    if (localzzaot != null) {
      return localzzaot;
    }
    localzzaot = this.bll.zza(this.bln, this.blm);
    this.bkU = localzzaot;
    return localzzaot;
  }
  
  public static zzaou zza(zzapx<?> paramzzapx, Object paramObject)
  {
    return new zza(paramObject, paramzzapx, false, null, null);
  }
  
  public static zzaou zzb(zzapx<?> paramzzapx, Object paramObject)
  {
    if (paramzzapx.bz() == paramzzapx.by()) {}
    for (boolean bool = true;; bool = false) {
      return new zza(paramObject, paramzzapx, bool, null, null);
    }
  }
  
  public void zza(zzaqa paramzzaqa, T paramT)
    throws IOException
  {
    if (this.blj == null)
    {
      bd().zza(paramzzaqa, paramT);
      return;
    }
    if (paramT == null)
    {
      paramzzaqa.bx();
      return;
    }
    zzapi.zzb(this.blj.zza(paramT, this.blm.bz(), this.bll.bkS), paramzzaqa);
  }
  
  public T zzb(zzapy paramzzapy)
    throws IOException
  {
    if (this.blk == null) {
      return (T)bd().zzb(paramzzapy);
    }
    paramzzapy = zzapi.zzh(paramzzapy);
    if (paramzzapy.aV()) {
      return null;
    }
    try
    {
      paramzzapy = this.blk.zzb(paramzzapy, this.blm.bz(), this.bll.bkR);
      return paramzzapy;
    }
    catch (zzaol paramzzapy)
    {
      throw paramzzapy;
    }
    catch (Exception paramzzapy)
    {
      throw new zzaol(paramzzapy);
    }
  }
  
  private static class zza
    implements zzaou
  {
    private final zzaop<?> blj;
    private final zzaog<?> blk;
    private final zzapx<?> blo;
    private final boolean blp;
    private final Class<?> blq;
    
    private zza(Object paramObject, zzapx<?> paramzzapx, boolean paramBoolean, Class<?> paramClass)
    {
      zzaop localzzaop;
      if ((paramObject instanceof zzaop))
      {
        localzzaop = (zzaop)paramObject;
        this.blj = localzzaop;
        if (!(paramObject instanceof zzaog)) {
          break label85;
        }
        paramObject = (zzaog)paramObject;
        label35:
        this.blk = ((zzaog)paramObject);
        if ((this.blj == null) && (this.blk == null)) {
          break label90;
        }
      }
      label85:
      label90:
      for (boolean bool = true;; bool = false)
      {
        zzaoz.zzbs(bool);
        this.blo = paramzzapx;
        this.blp = paramBoolean;
        this.blq = paramClass;
        return;
        localzzaop = null;
        break;
        paramObject = null;
        break label35;
      }
    }
    
    public <T> zzaot<T> zza(zzaob paramzzaob, zzapx<T> paramzzapx)
    {
      boolean bool;
      if (this.blo != null) {
        if ((this.blo.equals(paramzzapx)) || ((this.blp) && (this.blo.bz() == paramzzapx.by()))) {
          bool = true;
        }
      }
      while (bool)
      {
        return new zzaos(this.blj, this.blk, paramzzaob, paramzzapx, this, null);
        bool = false;
        continue;
        bool = this.blq.isAssignableFrom(paramzzapx.by());
      }
      return null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaos.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */