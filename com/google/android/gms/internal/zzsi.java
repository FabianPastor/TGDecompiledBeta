package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzsi<T>
{
  private static zza BL = null;
  private static int BM = 0;
  private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
  private static final Object zzaox = new Object();
  private T BN = null;
  protected final String zzbcn;
  protected final T zzbco;
  
  protected zzsi(String paramString, T paramT)
  {
    this.zzbcn = paramString;
    this.zzbco = paramT;
  }
  
  public static zzsi<Float> zza(String paramString, Float paramFloat)
  {
    new zzsi(paramString, paramFloat)
    {
      protected Float zzhm(String paramAnonymousString)
      {
        return zzsi.zzauh().zzb(this.zzbcn, (Float)this.zzbco);
      }
    };
  }
  
  public static zzsi<Integer> zza(String paramString, Integer paramInteger)
  {
    new zzsi(paramString, paramInteger)
    {
      protected Integer zzhl(String paramAnonymousString)
      {
        return zzsi.zzauh().zzb(this.zzbcn, (Integer)this.zzbco);
      }
    };
  }
  
  public static zzsi<Long> zza(String paramString, Long paramLong)
  {
    new zzsi(paramString, paramLong)
    {
      protected Long zzhk(String paramAnonymousString)
      {
        return zzsi.zzauh().getLong(this.zzbcn, (Long)this.zzbco);
      }
    };
  }
  
  public static zzsi<String> zzaa(String paramString1, String paramString2)
  {
    new zzsi(paramString1, paramString2)
    {
      protected String zzhn(String paramAnonymousString)
      {
        return zzsi.zzauh().getString(this.zzbcn, (String)this.zzbco);
      }
    };
  }
  
  public static zzsi<Boolean> zzk(String paramString, boolean paramBoolean)
  {
    new zzsi(paramString, Boolean.valueOf(paramBoolean))
    {
      protected Boolean zzhj(String paramAnonymousString)
      {
        return zzsi.zzauh().zza(this.zzbcn, (Boolean)this.zzbco);
      }
    };
  }
  
  public final T get()
  {
    try
    {
      Object localObject1 = zzhi(this.zzbcn);
      return (T)localObject1;
    }
    catch (SecurityException localSecurityException)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        Object localObject2 = zzhi(this.zzbcn);
        return (T)localObject2;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  protected abstract T zzhi(String paramString);
  
  private static abstract interface zza
  {
    public abstract Long getLong(String paramString, Long paramLong);
    
    public abstract String getString(String paramString1, String paramString2);
    
    public abstract Boolean zza(String paramString, Boolean paramBoolean);
    
    public abstract Float zzb(String paramString, Float paramFloat);
    
    public abstract Integer zzb(String paramString, Integer paramInteger);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */