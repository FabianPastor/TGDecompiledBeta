package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class zzaot
{
  private final List<zzapl> boc = new ArrayList();
  private zzapt bom = zzapt.boW;
  private zzapi bon = zzapi.box;
  private zzaor boo = zzaoq.bnU;
  private final Map<Type, zzaou<?>> bop = new HashMap();
  private final List<zzapl> boq = new ArrayList();
  private int bor = 2;
  private int bos = 2;
  private boolean bot = true;
  
  private void zza(String paramString, int paramInt1, int paramInt2, List<zzapl> paramList)
  {
    if ((paramString != null) && (!"".equals(paramString.trim()))) {}
    for (paramString = new zzaon(paramString);; paramString = new zzaon(paramInt1, paramInt2))
    {
      paramList.add(zzapj.zza(zzaqo.zzr(java.util.Date.class), paramString));
      paramList.add(zzapj.zza(zzaqo.zzr(Timestamp.class), paramString));
      paramList.add(zzapj.zza(zzaqo.zzr(java.sql.Date.class), paramString));
      do
      {
        return;
      } while ((paramInt1 == 2) || (paramInt2 == 2));
    }
  }
  
  public zzaot aR()
  {
    this.bot = false;
    return this;
  }
  
  public zzaos aS()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.boc);
    Collections.reverse(localArrayList);
    localArrayList.addAll(this.boq);
    zza(null, this.bor, this.bos, localArrayList);
    return new zzaos(this.bom, this.boo, this.bop, false, false, false, this.bot, false, false, this.bon, localArrayList);
  }
  
  public zzaot zza(Type paramType, Object paramObject)
  {
    if (((paramObject instanceof zzapg)) || ((paramObject instanceof zzaox)) || ((paramObject instanceof zzaou)) || ((paramObject instanceof zzapk))) {}
    for (boolean bool = true;; bool = false)
    {
      zzapq.zzbt(bool);
      if ((paramObject instanceof zzaou)) {
        this.bop.put(paramType, (zzaou)paramObject);
      }
      if (((paramObject instanceof zzapg)) || ((paramObject instanceof zzaox)))
      {
        zzaqo localzzaqo = zzaqo.zzl(paramType);
        this.boc.add(zzapj.zzb(localzzaqo, paramObject));
      }
      if ((paramObject instanceof zzapk)) {
        this.boc.add(zzaqn.zza(zzaqo.zzl(paramType), (zzapk)paramObject));
      }
      return this;
    }
  }
  
  public zzaot zza(zzaoo... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      zzaoo localzzaoo = paramVarArgs[i];
      this.bom = this.bom.zza(localzzaoo, true, true);
      i += 1;
    }
    return this;
  }
  
  public zzaot zzf(int... paramVarArgs)
  {
    this.bom = this.bom.zzg(paramVarArgs);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */