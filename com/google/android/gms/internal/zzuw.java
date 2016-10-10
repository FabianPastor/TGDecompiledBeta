package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzuw<T>
{
  private final int zzbae;
  private final String zzbaf;
  private final T zzbag;
  
  private zzuw(int paramInt, String paramString, T paramT)
  {
    this.zzbae = paramInt;
    this.zzbaf = paramString;
    this.zzbag = paramT;
    zzva.zzbhm().zza(this);
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
    return (T)zzva.zzbhn().zzb(this);
  }
  
  public String getKey()
  {
    return this.zzbaf;
  }
  
  public int getSource()
  {
    return this.zzbae;
  }
  
  protected abstract T zza(zzuz paramzzuz);
  
  public T zzkq()
  {
    return (T)this.zzbag;
  }
  
  public static class zza
    extends zzuw<Boolean>
  {
    public zza(int paramInt, String paramString, Boolean paramBoolean)
    {
      super(paramString, paramBoolean, null);
    }
    
    public Boolean zzb(zzuz paramzzuz)
    {
      try
      {
        boolean bool = paramzzuz.getBooleanFlagValue(getKey(), ((Boolean)zzkq()).booleanValue(), getSource());
        return Boolean.valueOf(bool);
      }
      catch (RemoteException paramzzuz) {}
      return (Boolean)zzkq();
    }
  }
  
  public static class zzb
    extends zzuw<Integer>
  {
    public zzb(int paramInt, String paramString, Integer paramInteger)
    {
      super(paramString, paramInteger, null);
    }
    
    public Integer zzc(zzuz paramzzuz)
    {
      try
      {
        int i = paramzzuz.getIntFlagValue(getKey(), ((Integer)zzkq()).intValue(), getSource());
        return Integer.valueOf(i);
      }
      catch (RemoteException paramzzuz) {}
      return (Integer)zzkq();
    }
  }
  
  public static class zzc
    extends zzuw<Long>
  {
    public zzc(int paramInt, String paramString, Long paramLong)
    {
      super(paramString, paramLong, null);
    }
    
    public Long zzd(zzuz paramzzuz)
    {
      try
      {
        long l = paramzzuz.getLongFlagValue(getKey(), ((Long)zzkq()).longValue(), getSource());
        return Long.valueOf(l);
      }
      catch (RemoteException paramzzuz) {}
      return (Long)zzkq();
    }
  }
  
  public static class zzd
    extends zzuw<String>
  {
    public zzd(int paramInt, String paramString1, String paramString2)
    {
      super(paramString1, paramString2, null);
    }
    
    public String zze(zzuz paramzzuz)
    {
      try
      {
        paramzzuz = paramzzuz.getStringFlagValue(getKey(), (String)zzkq(), getSource());
        return paramzzuz;
      }
      catch (RemoteException paramzzuz) {}
      return (String)zzkq();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzuw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */