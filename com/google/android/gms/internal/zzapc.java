package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class zzapc
  implements zzaou, Cloneable
{
  public static final zzapc blF = new zzapc();
  private double blG = -1.0D;
  private int blH = 136;
  private boolean blI = true;
  private List<zzanx> blJ = Collections.emptyList();
  private List<zzanx> blK = Collections.emptyList();
  
  private boolean zza(zzaox paramzzaox)
  {
    return (paramzzaox == null) || (paramzzaox.bf() <= this.blG);
  }
  
  private boolean zza(zzaox paramzzaox, zzaoy paramzzaoy)
  {
    return (zza(paramzzaox)) && (zza(paramzzaoy));
  }
  
  private boolean zza(zzaoy paramzzaoy)
  {
    return (paramzzaoy == null) || (paramzzaoy.bf() > this.blG);
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
  
  protected zzapc bh()
  {
    try
    {
      zzapc localzzapc = (zzapc)super.clone();
      return localzzapc;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError();
    }
  }
  
  public <T> zzaot<T> zza(final zzaob paramzzaob, final zzapx<T> paramzzapx)
  {
    Class localClass = paramzzapx.by();
    final boolean bool1 = zza(localClass, true);
    final boolean bool2 = zza(localClass, false);
    if ((!bool1) && (!bool2)) {
      return null;
    }
    new zzaot()
    {
      private zzaot<T> bkU;
      
      private zzaot<T> bd()
      {
        zzaot localzzaot = this.bkU;
        if (localzzaot != null) {
          return localzzaot;
        }
        localzzaot = paramzzaob.zza(zzapc.this, paramzzapx);
        this.bkU = localzzaot;
        return localzzaot;
      }
      
      public void zza(zzaqa paramAnonymouszzaqa, T paramAnonymousT)
        throws IOException
      {
        if (bool1)
        {
          paramAnonymouszzaqa.bx();
          return;
        }
        bd().zza(paramAnonymouszzaqa, paramAnonymousT);
      }
      
      public T zzb(zzapy paramAnonymouszzapy)
        throws IOException
      {
        if (bool2)
        {
          paramAnonymouszzapy.skipValue();
          return null;
        }
        return (T)bd().zzb(paramAnonymouszzapy);
      }
    };
  }
  
  public zzapc zza(zzanx paramzzanx, boolean paramBoolean1, boolean paramBoolean2)
  {
    zzapc localzzapc = bh();
    if (paramBoolean1)
    {
      localzzapc.blJ = new ArrayList(this.blJ);
      localzzapc.blJ.add(paramzzanx);
    }
    if (paramBoolean2)
    {
      localzzapc.blK = new ArrayList(this.blK);
      localzzapc.blK.add(paramzzanx);
    }
    return localzzapc;
  }
  
  public boolean zza(Class<?> paramClass, boolean paramBoolean)
  {
    if ((this.blG != -1.0D) && (!zza((zzaox)paramClass.getAnnotation(zzaox.class), (zzaoy)paramClass.getAnnotation(zzaoy.class)))) {
      return true;
    }
    if ((!this.blI) && (zzn(paramClass))) {
      return true;
    }
    if (zzm(paramClass)) {
      return true;
    }
    if (paramBoolean) {}
    for (Object localObject = this.blJ;; localObject = this.blK)
    {
      localObject = ((List)localObject).iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!((zzanx)((Iterator)localObject).next()).zzh(paramClass));
      return true;
    }
    return false;
  }
  
  public boolean zza(Field paramField, boolean paramBoolean)
  {
    if ((this.blH & paramField.getModifiers()) != 0) {
      return true;
    }
    if ((this.blG != -1.0D) && (!zza((zzaox)paramField.getAnnotation(zzaox.class), (zzaoy)paramField.getAnnotation(zzaoy.class)))) {
      return true;
    }
    if (paramField.isSynthetic()) {
      return true;
    }
    if ((!this.blI) && (zzn(paramField.getType()))) {
      return true;
    }
    if (zzm(paramField.getType())) {
      return true;
    }
    if (paramBoolean) {}
    for (Object localObject = this.blJ; !((List)localObject).isEmpty(); localObject = this.blK)
    {
      paramField = new zzany(paramField);
      localObject = ((List)localObject).iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!((zzanx)((Iterator)localObject).next()).zza(paramField));
      return true;
    }
    return false;
  }
  
  public zzapc zzg(int... paramVarArgs)
  {
    int i = 0;
    zzapc localzzapc = bh();
    localzzapc.blH = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      localzzapc.blH = (paramVarArgs[i] | localzzapc.blH);
      i += 1;
    }
    return localzzapc;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */