package com.google.android.gms.internal;

import java.io.IOException;

final class zzapj<T>
  extends zzapk<T>
{
  private final zzapg<T> boA;
  private final zzaox<T> boB;
  private final zzaos boC;
  private final zzaqo<T> boD;
  private final zzapl boE;
  private zzapk<T> bol;
  
  private zzapj(zzapg<T> paramzzapg, zzaox<T> paramzzaox, zzaos paramzzaos, zzaqo<T> paramzzaqo, zzapl paramzzapl)
  {
    this.boA = paramzzapg;
    this.boB = paramzzaox;
    this.boC = paramzzaos;
    this.boD = paramzzaqo;
    this.boE = paramzzapl;
  }
  
  private zzapk<T> bg()
  {
    zzapk localzzapk = this.bol;
    if (localzzapk != null) {
      return localzzapk;
    }
    localzzapk = this.boC.zza(this.boE, this.boD);
    this.bol = localzzapk;
    return localzzapk;
  }
  
  public static zzapl zza(zzaqo<?> paramzzaqo, Object paramObject)
  {
    return new zza(paramObject, paramzzaqo, false, null, null);
  }
  
  public static zzapl zzb(zzaqo<?> paramzzaqo, Object paramObject)
  {
    if (paramzzaqo.bC() == paramzzaqo.bB()) {}
    for (boolean bool = true;; bool = false) {
      return new zza(paramObject, paramzzaqo, bool, null, null);
    }
  }
  
  public void zza(zzaqr paramzzaqr, T paramT)
    throws IOException
  {
    if (this.boA == null)
    {
      bg().zza(paramzzaqr, paramT);
      return;
    }
    if (paramT == null)
    {
      paramzzaqr.bA();
      return;
    }
    zzapz.zzb(this.boA.zza(paramT, this.boD.bC(), this.boC.boj), paramzzaqr);
  }
  
  public T zzb(zzaqp paramzzaqp)
    throws IOException
  {
    if (this.boB == null) {
      return (T)bg().zzb(paramzzaqp);
    }
    paramzzaqp = zzapz.zzh(paramzzaqp);
    if (paramzzaqp.aY()) {
      return null;
    }
    try
    {
      paramzzaqp = this.boB.zzb(paramzzaqp, this.boD.bC(), this.boC.boi);
      return paramzzaqp;
    }
    catch (zzapc paramzzaqp)
    {
      throw paramzzaqp;
    }
    catch (Exception paramzzaqp)
    {
      throw new zzapc(paramzzaqp);
    }
  }
  
  private static class zza
    implements zzapl
  {
    private final zzapg<?> boA;
    private final zzaox<?> boB;
    private final zzaqo<?> boF;
    private final boolean boG;
    private final Class<?> boH;
    
    private zza(Object paramObject, zzaqo<?> paramzzaqo, boolean paramBoolean, Class<?> paramClass)
    {
      zzapg localzzapg;
      if ((paramObject instanceof zzapg))
      {
        localzzapg = (zzapg)paramObject;
        this.boA = localzzapg;
        if (!(paramObject instanceof zzaox)) {
          break label85;
        }
        paramObject = (zzaox)paramObject;
        label35:
        this.boB = ((zzaox)paramObject);
        if ((this.boA == null) && (this.boB == null)) {
          break label90;
        }
      }
      label85:
      label90:
      for (boolean bool = true;; bool = false)
      {
        zzapq.zzbt(bool);
        this.boF = paramzzaqo;
        this.boG = paramBoolean;
        this.boH = paramClass;
        return;
        localzzapg = null;
        break;
        paramObject = null;
        break label35;
      }
    }
    
    public <T> zzapk<T> zza(zzaos paramzzaos, zzaqo<T> paramzzaqo)
    {
      boolean bool;
      if (this.boF != null) {
        if ((this.boF.equals(paramzzaqo)) || ((this.boG) && (this.boF.bC() == paramzzaqo.bB()))) {
          bool = true;
        }
      }
      while (bool)
      {
        return new zzapj(this.boA, this.boB, paramzzaos, paramzzaqo, this, null);
        bool = false;
        continue;
        bool = this.boH.isAssignableFrom(paramzzaqo.bB());
      }
      return null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */