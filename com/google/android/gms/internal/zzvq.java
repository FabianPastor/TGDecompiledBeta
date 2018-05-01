package com.google.android.gms.internal;

import android.os.RemoteException;

public abstract class zzvq<T>
{
  private final int zzbcm;
  private final String zzbcn;
  private final T zzbco;
  
  private zzvq(int paramInt, String paramString, T paramT)
  {
    this.zzbcm = paramInt;
    this.zzbcn = paramString;
    this.zzbco = paramT;
    zzvu.zzbhe().zza(this);
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
    return (T)zzvu.zzbhf().zzb(this);
  }
  
  public String getKey()
  {
    return this.zzbcn;
  }
  
  public int getSource()
  {
    return this.zzbcm;
  }
  
  protected abstract T zza(zzvt paramzzvt);
  
  public T zzlp()
  {
    return (T)this.zzbco;
  }
  
  public static class zza
    extends zzvq<Boolean>
  {
    public zza(int paramInt, String paramString, Boolean paramBoolean)
    {
      super(paramString, paramBoolean, null);
    }
    
    public Boolean zzb(zzvt paramzzvt)
    {
      try
      {
        boolean bool = paramzzvt.getBooleanFlagValue(getKey(), ((Boolean)zzlp()).booleanValue(), getSource());
        return Boolean.valueOf(bool);
      }
      catch (RemoteException paramzzvt) {}
      return (Boolean)zzlp();
    }
  }
  
  public static class zzb
    extends zzvq<Integer>
  {
    public zzb(int paramInt, String paramString, Integer paramInteger)
    {
      super(paramString, paramInteger, null);
    }
    
    public Integer zzc(zzvt paramzzvt)
    {
      try
      {
        int i = paramzzvt.getIntFlagValue(getKey(), ((Integer)zzlp()).intValue(), getSource());
        return Integer.valueOf(i);
      }
      catch (RemoteException paramzzvt) {}
      return (Integer)zzlp();
    }
  }
  
  public static class zzc
    extends zzvq<Long>
  {
    public zzc(int paramInt, String paramString, Long paramLong)
    {
      super(paramString, paramLong, null);
    }
    
    public Long zzd(zzvt paramzzvt)
    {
      try
      {
        long l = paramzzvt.getLongFlagValue(getKey(), ((Long)zzlp()).longValue(), getSource());
        return Long.valueOf(l);
      }
      catch (RemoteException paramzzvt) {}
      return (Long)zzlp();
    }
  }
  
  public static class zzd
    extends zzvq<String>
  {
    public zzd(int paramInt, String paramString1, String paramString2)
    {
      super(paramString1, paramString2, null);
    }
    
    public String zze(zzvt paramzzvt)
    {
      try
      {
        paramzzvt = paramzzvt.getStringFlagValue(getKey(), (String)zzlp(), getSource());
        return paramzzvt;
      }
      catch (RemoteException paramzzvt) {}
      return (String)zzlp();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzvq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */