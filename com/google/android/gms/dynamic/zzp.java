package com.google.android.gms.dynamic;

import android.content.Context;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzbq;

public abstract class zzp<T>
{
  private final String zzgwn;
  private T zzgwo;
  
  protected zzp(String paramString)
  {
    this.zzgwn = paramString;
  }
  
  protected final T zzde(Context paramContext)
    throws zzq
  {
    if (this.zzgwo == null)
    {
      zzbq.checkNotNull(paramContext);
      paramContext = com.google.android.gms.common.zzp.getRemoteContext(paramContext);
      if (paramContext == null) {
        throw new zzq("Could not get remote context.");
      }
      paramContext = paramContext.getClassLoader();
    }
    try
    {
      this.zzgwo = zze((IBinder)paramContext.loadClass(this.zzgwn).newInstance());
      return (T)this.zzgwo;
    }
    catch (ClassNotFoundException paramContext)
    {
      throw new zzq("Could not load creator class.", paramContext);
    }
    catch (InstantiationException paramContext)
    {
      throw new zzq("Could not instantiate creator.", paramContext);
    }
    catch (IllegalAccessException paramContext)
    {
      throw new zzq("Could not access creator.", paramContext);
    }
  }
  
  protected abstract T zze(IBinder paramIBinder);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */