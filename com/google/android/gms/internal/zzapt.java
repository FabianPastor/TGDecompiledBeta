package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class zzapt
  implements zzapl, Cloneable
{
  public static final zzapt boW = new zzapt();
  private double boX = -1.0D;
  private int boY = 136;
  private boolean boZ = true;
  private List<zzaoo> bpa = Collections.emptyList();
  private List<zzaoo> bpb = Collections.emptyList();
  
  private boolean zza(zzapo paramzzapo)
  {
    return (paramzzapo == null) || (paramzzapo.bi() <= this.boX);
  }
  
  private boolean zza(zzapo paramzzapo, zzapp paramzzapp)
  {
    return (zza(paramzzapo)) && (zza(paramzzapp));
  }
  
  private boolean zza(zzapp paramzzapp)
  {
    return (paramzzapp == null) || (paramzzapp.bi() > this.boX);
  }
  
  private boolean zzm(Class<?> paramClass)
  {
    return (!Enum.class.isAssignableFrom(paramClass)) && ((paramClass.isAnonymousClass()) || (paramClass.isLocalClass()));
  }
  
  private boolean zzn(Class<?> paramClass)
  {
    return (paramClass.isMemberClass()) && (!zzo(paramClass));
  }
  
  private boolean zzo(Class<?> paramClass)
  {
    return (paramClass.getModifiers() & 0x8) != 0;
  }
  
  protected zzapt bk()
  {
    try
    {
      zzapt localzzapt = (zzapt)super.clone();
      return localzzapt;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError();
    }
  }
  
  public <T> zzapk<T> zza(final zzaos paramzzaos, final zzaqo<T> paramzzaqo)
  {
    Class localClass = paramzzaqo.bB();
    final boolean bool1 = zza(localClass, true);
    final boolean bool2 = zza(localClass, false);
    if ((!bool1) && (!bool2)) {
      return null;
    }
    new zzapk()
    {
      private zzapk<T> bol;
      
      private zzapk<T> bg()
      {
        zzapk localzzapk = this.bol;
        if (localzzapk != null) {
          return localzzapk;
        }
        localzzapk = paramzzaos.zza(zzapt.this, paramzzaqo);
        this.bol = localzzapk;
        return localzzapk;
      }
      
      public void zza(zzaqr paramAnonymouszzaqr, T paramAnonymousT)
        throws IOException
      {
        if (bool1)
        {
          paramAnonymouszzaqr.bA();
          return;
        }
        bg().zza(paramAnonymouszzaqr, paramAnonymousT);
      }
      
      public T zzb(zzaqp paramAnonymouszzaqp)
        throws IOException
      {
        if (bool2)
        {
          paramAnonymouszzaqp.skipValue();
          return null;
        }
        return (T)bg().zzb(paramAnonymouszzaqp);
      }
    };
  }
  
  public zzapt zza(zzaoo paramzzaoo, boolean paramBoolean1, boolean paramBoolean2)
  {
    zzapt localzzapt = bk();
    if (paramBoolean1)
    {
      localzzapt.bpa = new ArrayList(this.bpa);
      localzzapt.bpa.add(paramzzaoo);
    }
    if (paramBoolean2)
    {
      localzzapt.bpb = new ArrayList(this.bpb);
      localzzapt.bpb.add(paramzzaoo);
    }
    return localzzapt;
  }
  
  public boolean zza(Class<?> paramClass, boolean paramBoolean)
  {
    if ((this.boX != -1.0D) && (!zza((zzapo)paramClass.getAnnotation(zzapo.class), (zzapp)paramClass.getAnnotation(zzapp.class)))) {
      return true;
    }
    if ((!this.boZ) && (zzn(paramClass))) {
      return true;
    }
    if (zzm(paramClass)) {
      return true;
    }
    if (paramBoolean) {}
    for (Object localObject = this.bpa;; localObject = this.bpb)
    {
      localObject = ((List)localObject).iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!((zzaoo)((Iterator)localObject).next()).zzh(paramClass));
      return true;
    }
    return false;
  }
  
  public boolean zza(Field paramField, boolean paramBoolean)
  {
    if ((this.boY & paramField.getModifiers()) != 0) {
      return true;
    }
    if ((this.boX != -1.0D) && (!zza((zzapo)paramField.getAnnotation(zzapo.class), (zzapp)paramField.getAnnotation(zzapp.class)))) {
      return true;
    }
    if (paramField.isSynthetic()) {
      return true;
    }
    if ((!this.boZ) && (zzn(paramField.getType()))) {
      return true;
    }
    if (zzm(paramField.getType())) {
      return true;
    }
    if (paramBoolean) {}
    for (Object localObject = this.bpa; !((List)localObject).isEmpty(); localObject = this.bpb)
    {
      paramField = new zzaop(paramField);
      localObject = ((List)localObject).iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!((zzaoo)((Iterator)localObject).next()).zza(paramField));
      return true;
    }
    return false;
  }
  
  public zzapt zzg(int... paramVarArgs)
  {
    int i = 0;
    zzapt localzzapt = bk();
    localzzapt.boY = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      localzzapt.boY = (paramVarArgs[i] | localzzapt.boY);
      i += 1;
    }
    return localzzapt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */