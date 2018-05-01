package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzaqa<T>
{
  private final int zzAW;
  private final String zzAX;
  private final T zzAY;
  
  private zzaqa(int paramInt, String paramString, T paramT)
  {
    this.zzAW = paramInt;
    this.zzAX = paramString;
    this.zzAY = paramT;
    zzaqe.zzDE().zza(this);
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
    return (T)zzaqe.zzDF().zzb(this);
  }
  
  public String getKey()
  {
    return this.zzAX;
  }
  
  public int getSource()
  {
    return this.zzAW;
  }
  
  protected abstract T zza(zzaqd paramzzaqd);
  
  public T zzfr()
  {
    return (T)this.zzAY;
  }
  
  public static class zza
    extends zzaqa<Boolean>
  {
    public zza(int paramInt, String paramString, Boolean paramBoolean)
    {
      super(paramString, paramBoolean, null);
    }
    
    public Boolean zzb(zzaqd paramzzaqd)
    {
      try
      {
        boolean bool = paramzzaqd.getBooleanFlagValue(getKey(), ((Boolean)zzfr()).booleanValue(), getSource());
        return Boolean.valueOf(bool);
      }
      catch (RemoteException paramzzaqd) {}
      return (Boolean)zzfr();
    }
  }
  
  public static class zzb
    extends zzaqa<Integer>
  {
    public zzb(int paramInt, String paramString, Integer paramInteger)
    {
      super(paramString, paramInteger, null);
    }
    
    public Integer zzc(zzaqd paramzzaqd)
    {
      try
      {
        int i = paramzzaqd.getIntFlagValue(getKey(), ((Integer)zzfr()).intValue(), getSource());
        return Integer.valueOf(i);
      }
      catch (RemoteException paramzzaqd) {}
      return (Integer)zzfr();
    }
  }
  
  public static class zzc
    extends zzaqa<Long>
  {
    public zzc(int paramInt, String paramString, Long paramLong)
    {
      super(paramString, paramLong, null);
    }
    
    public Long zzd(zzaqd paramzzaqd)
    {
      try
      {
        long l = paramzzaqd.getLongFlagValue(getKey(), ((Long)zzfr()).longValue(), getSource());
        return Long.valueOf(l);
      }
      catch (RemoteException paramzzaqd) {}
      return (Long)zzfr();
    }
  }
  
  public static class zzd
    extends zzaqa<String>
  {
    public zzd(int paramInt, String paramString1, String paramString2)
    {
      super(paramString1, paramString2, null);
    }
    
    public String zze(zzaqd paramzzaqd)
    {
      try
      {
        paramzzaqd = paramzzaqd.getStringFlagValue(getKey(), (String)zzfr(), getSource());
        return paramzzaqd;
      }
      catch (RemoteException paramzzaqd) {}
      return (String)zzfr();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */