package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzrs<T>
{
  private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
  private static zza zB;
  private static int zC;
  private static final Object zzaok = new Object();
  private T zD = null;
  protected final String zzbaf;
  protected final T zzbag;
  
  static
  {
    zB = null;
    zC = 0;
  }
  
  protected zzrs(String paramString, T paramT)
  {
    this.zzbaf = paramString;
    this.zzbag = paramT;
  }
  
  public static zzrs<Float> zza(String paramString, Float paramFloat)
  {
    new zzrs(paramString, paramFloat)
    {
      protected Float zzhk(String paramAnonymousString)
      {
        return zzrs.zzasy().zzb(this.zzbaf, (Float)this.zzbag);
      }
    };
  }
  
  public static zzrs<Integer> zza(String paramString, Integer paramInteger)
  {
    new zzrs(paramString, paramInteger)
    {
      protected Integer zzhj(String paramAnonymousString)
      {
        return zzrs.zzasy().zzb(this.zzbaf, (Integer)this.zzbag);
      }
    };
  }
  
  public static zzrs<Long> zza(String paramString, Long paramLong)
  {
    new zzrs(paramString, paramLong)
    {
      protected Long zzhi(String paramAnonymousString)
      {
        return zzrs.zzasy().getLong(this.zzbaf, (Long)this.zzbag);
      }
    };
  }
  
  public static zzrs<String> zzab(String paramString1, String paramString2)
  {
    new zzrs(paramString1, paramString2)
    {
      protected String zzhl(String paramAnonymousString)
      {
        return zzrs.zzasy().getString(this.zzbaf, (String)this.zzbag);
      }
    };
  }
  
  public static zzrs<Boolean> zzm(String paramString, boolean paramBoolean)
  {
    new zzrs(paramString, Boolean.valueOf(paramBoolean))
    {
      protected Boolean zzhh(String paramAnonymousString)
      {
        return zzrs.zzasy().zza(this.zzbaf, (Boolean)this.zzbag);
      }
    };
  }
  
  public final T get()
  {
    try
    {
      Object localObject1 = zzhg(this.zzbaf);
      return (T)localObject1;
    }
    catch (SecurityException localSecurityException)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        Object localObject2 = zzhg(this.zzbaf);
        return (T)localObject2;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  protected abstract T zzhg(String paramString);
  
  private static abstract interface zza
  {
    public abstract Long getLong(String paramString, Long paramLong);
    
    public abstract String getString(String paramString1, String paramString2);
    
    public abstract Boolean zza(String paramString, Boolean paramBoolean);
    
    public abstract Float zzb(String paramString, Float paramFloat);
    
    public abstract Integer zzb(String paramString, Integer paramInteger);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */