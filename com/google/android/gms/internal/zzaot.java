package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzaot<T>
{
  public abstract void zza(zzaqa paramzzaqa, T paramT)
    throws IOException;
  
  public abstract T zzb(zzapy paramzzapy)
    throws IOException;
  
  public final zzaoh zzco(T paramT)
  {
    try
    {
      zzapp localzzapp = new zzapp();
      zza(localzzapp, paramT);
      paramT = localzzapp.br();
      return paramT;
    }
    catch (IOException paramT)
    {
      throw new zzaoi(paramT);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */