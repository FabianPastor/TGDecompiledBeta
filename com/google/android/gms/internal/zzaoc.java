package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class zzaoc
{
  private final List<zzaou> bkL = new ArrayList();
  private zzapc bkV = zzapc.blF;
  private zzaor bkW = zzaor.blg;
  private zzaoa bkX = zzanz.bkD;
  private final Map<Type, zzaod<?>> bkY = new HashMap();
  private final List<zzaou> bkZ = new ArrayList();
  private int bla = 2;
  private int blb = 2;
  private boolean blc = true;
  
  private void zza(String paramString, int paramInt1, int paramInt2, List<zzaou> paramList)
  {
    if ((paramString != null) && (!"".equals(paramString.trim()))) {}
    for (paramString = new zzanw(paramString);; paramString = new zzanw(paramInt1, paramInt2))
    {
      paramList.add(zzaos.zza(zzapx.zzr(java.util.Date.class), paramString));
      paramList.add(zzaos.zza(zzapx.zzr(Timestamp.class), paramString));
      paramList.add(zzaos.zza(zzapx.zzr(java.sql.Date.class), paramString));
      do
      {
        return;
      } while ((paramInt1 == 2) || (paramInt2 == 2));
    }
  }
  
  public zzaoc aO()
  {
    this.blc = false;
    return this;
  }
  
  public zzaob aP()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.bkL);
    Collections.reverse(localArrayList);
    localArrayList.addAll(this.bkZ);
    zza(null, this.bla, this.blb, localArrayList);
    return new zzaob(this.bkV, this.bkX, this.bkY, false, false, false, this.blc, false, false, this.bkW, localArrayList);
  }
  
  public zzaoc zza(Type paramType, Object paramObject)
  {
    if (((paramObject instanceof zzaop)) || ((paramObject instanceof zzaog)) || ((paramObject instanceof zzaod)) || ((paramObject instanceof zzaot))) {}
    for (boolean bool = true;; bool = false)
    {
      zzaoz.zzbs(bool);
      if ((paramObject instanceof zzaod)) {
        this.bkY.put(paramType, (zzaod)paramObject);
      }
      if (((paramObject instanceof zzaop)) || ((paramObject instanceof zzaog)))
      {
        zzapx localzzapx = zzapx.zzl(paramType);
        this.bkL.add(zzaos.zzb(localzzapx, paramObject));
      }
      if ((paramObject instanceof zzaot)) {
        this.bkL.add(zzapw.zza(zzapx.zzl(paramType), (zzaot)paramObject));
      }
      return this;
    }
  }
  
  public zzaoc zza(zzanx... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      zzanx localzzanx = paramVarArgs[i];
      this.bkV = this.bkV.zza(localzzanx, true, true);
      i += 1;
    }
    return this;
  }
  
  public zzaoc zzf(int... paramVarArgs)
  {
    this.bkV = this.bkV.zzg(paramVarArgs);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaoc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */