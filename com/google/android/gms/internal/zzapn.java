package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzapn<T>
{
  private final int zzAG;
  private final String zzAH;
  private final T zzAI;
  
  private zzapn(int paramInt, String paramString, T paramT)
  {
    this.zzAG = paramInt;
    this.zzAH = paramString;
    this.zzAI = paramT;
    zzapr.zzCQ().zza(this);
  }
  
  public static zza zzb(int paramInt, String paramString, Boolean paramBoolean)
  {
    return new zza(paramInt, paramString, paramBoolean);
  }
  
  public static zzb zzb(int paramInt1, String paramString, int paramInt2)
  {
    return new zzb(paramInt1, paramString, Integer.valueOf(paramInt2));
  }
  
  public static zzc zzb(int paramInt, String paramString, long paramLong)
  {
    return new zzc(paramInt, paramString, Long.valueOf(paramLong));
  }
  
  public static zzd zzc(int paramInt, String paramString1, String paramString2)
  {
    return new zzd(paramInt, paramString1, paramString2);
  }
  
  public T get()
  {
    return (T)zzapr.zzCR().zzb(this);
  }
  
  public String getKey()
  {
    return this.zzAH;
  }
  
  public int getSource()
  {
    return this.zzAG;
  }
  
  protected abstract T zza(zzapq paramzzapq);
  
  public T zzfm()
  {
    return (T)this.zzAI;
  }
  
  public static class zza
    extends zzapn<Boolean>
  {
    public zza(int paramInt, String paramString, Boolean paramBoolean)
    {
      super(paramString, paramBoolean, null);
    }
    
    public Boolean zzb(zzapq paramzzapq)
    {
      try
      {
        boolean bool = paramzzapq.getBooleanFlagValue(getKey(), ((Boolean)zzfm()).booleanValue(), getSource());
        return Boolean.valueOf(bool);
      }
      catch (RemoteException paramzzapq) {}
      return (Boolean)zzfm();
    }
  }
  
  public static class zzb
    extends zzapn<Integer>
  {
    public zzb(int paramInt, String paramString, Integer paramInteger)
    {
      super(paramString, paramInteger, null);
    }
    
    public Integer zzc(zzapq paramzzapq)
    {
      try
      {
        int i = paramzzapq.getIntFlagValue(getKey(), ((Integer)zzfm()).intValue(), getSource());
        return Integer.valueOf(i);
      }
      catch (RemoteException paramzzapq) {}
      return (Integer)zzfm();
    }
  }
  
  public static class zzc
    extends zzapn<Long>
  {
    public zzc(int paramInt, String paramString, Long paramLong)
    {
      super(paramString, paramLong, null);
    }
    
    public Long zzd(zzapq paramzzapq)
    {
      try
      {
        long l = paramzzapq.getLongFlagValue(getKey(), ((Long)zzfm()).longValue(), getSource());
        return Long.valueOf(l);
      }
      catch (RemoteException paramzzapq) {}
      return (Long)zzfm();
    }
  }
  
  public static class zzd
    extends zzapn<String>
  {
    public zzd(int paramInt, String paramString1, String paramString2)
    {
      super(paramString1, paramString2, null);
    }
    
    public String zze(zzapq paramzzapq)
    {
      try
      {
        paramzzapq = paramzzapq.getStringFlagValue(getKey(), (String)zzfm(), getSource());
        return paramzzapq;
      }
      catch (RemoteException paramzzapq) {}
      return (String)zzfm();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */