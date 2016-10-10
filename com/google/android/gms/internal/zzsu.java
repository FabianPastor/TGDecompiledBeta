package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zze;
import java.lang.reflect.Field;

public final class zzsu
{
  public static final zzb OA = new zzb()
  {
    public zzsu.zzb.zzb zza(Context paramAnonymousContext, String paramAnonymousString, zzsu.zzb.zza paramAnonymouszza)
    {
      zzsu.zzb.zzb localzzb = new zzsu.zzb.zzb();
      localzzb.OF = paramAnonymouszza.zzaa(paramAnonymousContext, paramAnonymousString);
      localzzb.OG = paramAnonymouszza.zzc(paramAnonymousContext, paramAnonymousString, true);
      if ((localzzb.OF == 0) && (localzzb.OG == 0))
      {
        localzzb.OH = 0;
        return localzzb;
      }
      if (localzzb.OF >= localzzb.OG)
      {
        localzzb.OH = -1;
        return localzzb;
      }
      localzzb.OH = 1;
      return localzzb;
    }
  };
  public static final zzb OB = new zzb()
  {
    public zzsu.zzb.zzb zza(Context paramAnonymousContext, String paramAnonymousString, zzsu.zzb.zza paramAnonymouszza)
    {
      zzsu.zzb.zzb localzzb = new zzsu.zzb.zzb();
      localzzb.OF = paramAnonymouszza.zzaa(paramAnonymousContext, paramAnonymousString);
      localzzb.OG = paramAnonymouszza.zzc(paramAnonymousContext, paramAnonymousString, true);
      if ((localzzb.OF == 0) && (localzzb.OG == 0))
      {
        localzzb.OH = 0;
        return localzzb;
      }
      if (localzzb.OG >= localzzb.OF)
      {
        localzzb.OH = 1;
        return localzzb;
      }
      localzzb.OH = -1;
      return localzzb;
    }
  };
  public static final zzb OC = new zzb()
  {
    public zzsu.zzb.zzb zza(Context paramAnonymousContext, String paramAnonymousString, zzsu.zzb.zza paramAnonymouszza)
    {
      zzsu.zzb.zzb localzzb = new zzsu.zzb.zzb();
      localzzb.OF = paramAnonymouszza.zzaa(paramAnonymousContext, paramAnonymousString);
      if (localzzb.OF != 0) {}
      for (localzzb.OG = paramAnonymouszza.zzc(paramAnonymousContext, paramAnonymousString, false); (localzzb.OF == 0) && (localzzb.OG == 0); localzzb.OG = paramAnonymouszza.zzc(paramAnonymousContext, paramAnonymousString, true))
      {
        localzzb.OH = 0;
        return localzzb;
      }
      if (localzzb.OG >= localzzb.OF)
      {
        localzzb.OH = 1;
        return localzzb;
      }
      localzzb.OH = -1;
      return localzzb;
    }
  };
  private static zzsv Ow;
  private static final zzsu.zzb.zza Ox = new zzsu.zzb.zza()
  {
    public int zzaa(Context paramAnonymousContext, String paramAnonymousString)
    {
      return zzsu.zzaa(paramAnonymousContext, paramAnonymousString);
    }
    
    public int zzc(Context paramAnonymousContext, String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      return zzsu.zzc(paramAnonymousContext, paramAnonymousString, paramAnonymousBoolean);
    }
  };
  public static final zzb Oy = new zzb()
  {
    public zzsu.zzb.zzb zza(Context paramAnonymousContext, String paramAnonymousString, zzsu.zzb.zza paramAnonymouszza)
    {
      zzsu.zzb.zzb localzzb = new zzsu.zzb.zzb();
      localzzb.OG = paramAnonymouszza.zzc(paramAnonymousContext, paramAnonymousString, true);
      if (localzzb.OG != 0) {
        localzzb.OH = 1;
      }
      do
      {
        return localzzb;
        localzzb.OF = paramAnonymouszza.zzaa(paramAnonymousContext, paramAnonymousString);
      } while (localzzb.OF == 0);
      localzzb.OH = -1;
      return localzzb;
    }
  };
  public static final zzb Oz = new zzb()
  {
    public zzsu.zzb.zzb zza(Context paramAnonymousContext, String paramAnonymousString, zzsu.zzb.zza paramAnonymouszza)
    {
      zzsu.zzb.zzb localzzb = new zzsu.zzb.zzb();
      localzzb.OF = paramAnonymouszza.zzaa(paramAnonymousContext, paramAnonymousString);
      if (localzzb.OF != 0) {
        localzzb.OH = -1;
      }
      do
      {
        return localzzb;
        localzzb.OG = paramAnonymouszza.zzc(paramAnonymousContext, paramAnonymousString, true);
      } while (localzzb.OG == 0);
      localzzb.OH = 1;
      return localzzb;
    }
  };
  private final Context OD;
  
  private zzsu(Context paramContext)
  {
    this.OD = ((Context)zzac.zzy(paramContext));
  }
  
  public static zzsu zza(Context paramContext, zzb paramzzb, String paramString)
    throws zzsu.zza
  {
    zzsu.zzb.zzb localzzb = paramzzb.zza(paramContext, paramString, Ox);
    int i = localzzb.OF;
    int j = localzzb.OG;
    Log.i("DynamiteModule", String.valueOf(paramString).length() + 68 + String.valueOf(paramString).length() + "Considering local module " + paramString + ":" + i + " and remote module " + paramString + ":" + j);
    if ((localzzb.OH == 0) || ((localzzb.OH == -1) && (localzzb.OF == 0)) || ((localzzb.OH == 1) && (localzzb.OG == 0)))
    {
      i = localzzb.OF;
      j = localzzb.OG;
      throw new zza(91 + "No acceptable module found. Local version is " + i + " and remote version is " + j + ".", null);
    }
    if (localzzb.OH == -1) {
      return zzac(paramContext, paramString);
    }
    if (localzzb.OH == 1) {
      try
      {
        localObject = zza(paramContext, paramString, localzzb.OG);
        return (zzsu)localObject;
      }
      catch (zza localzza)
      {
        Object localObject = String.valueOf(localzza.getMessage());
        if (((String)localObject).length() != 0) {}
        for (localObject = "Failed to load remote module: ".concat((String)localObject);; localObject = new String("Failed to load remote module: "))
        {
          Log.w("DynamiteModule", (String)localObject);
          if ((localzzb.OF == 0) || (
          {
            public int zzaa(Context paramAnonymousContext, String paramAnonymousString)
            {
              return this.OE;
            }
            
            public int zzc(Context paramAnonymousContext, String paramAnonymousString, boolean paramAnonymousBoolean)
            {
              return 0;
            }
          } != -1)) {
            break;
          }
          return zzac(paramContext, paramString);
        }
        throw new zza("Remote load failed. No local fallback found.", localzza, null);
      }
    }
    i = localzzb.OH;
    throw new zza(47 + "VersionPolicy returned invalid code:" + i, null);
  }
  
  private static zzsu zza(Context paramContext, String paramString, int paramInt)
    throws zzsu.zza
  {
    Log.i("DynamiteModule", String.valueOf(paramString).length() + 51 + "Selected remote version of " + paramString + ", version >= " + paramInt);
    zzsv localzzsv = zzcv(paramContext);
    if (localzzsv == null) {
      throw new zza("Failed to create IDynamiteLoader.", null);
    }
    try
    {
      paramContext = localzzsv.zza(zze.zzac(paramContext), paramString, paramInt);
      if (zze.zzae(paramContext) == null) {
        throw new zza("Failed to load remote module.", null);
      }
    }
    catch (RemoteException paramContext)
    {
      throw new zza("Failed to load remote module.", paramContext, null);
    }
    return new zzsu((Context)zze.zzae(paramContext));
  }
  
  public static int zzaa(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getApplicationContext().getClassLoader();
      Object localObject = String.valueOf("com.google.android.gms.dynamite.descriptors.");
      String str = String.valueOf("ModuleDescriptor");
      localObject = paramContext.loadClass(String.valueOf(localObject).length() + 1 + String.valueOf(paramString).length() + String.valueOf(str).length() + (String)localObject + paramString + "." + str);
      paramContext = ((Class)localObject).getDeclaredField("MODULE_ID");
      localObject = ((Class)localObject).getDeclaredField("MODULE_VERSION");
      if (!paramContext.get(null).equals(paramString))
      {
        paramContext = String.valueOf(paramContext.get(null));
        Log.e("DynamiteModule", String.valueOf(paramContext).length() + 51 + String.valueOf(paramString).length() + "Module descriptor id '" + paramContext + "' didn't match expected id '" + paramString + "'");
        return 0;
      }
      int i = ((Field)localObject).getInt(null);
      return i;
    }
    catch (ClassNotFoundException paramContext)
    {
      Log.w("DynamiteModule", String.valueOf(paramString).length() + 45 + "Local module descriptor class for " + paramString + " not found.");
      return 0;
    }
    catch (Exception paramContext)
    {
      paramContext = String.valueOf(paramContext.getMessage());
      if (paramContext.length() == 0) {}
    }
    for (paramContext = "Failed to load module descriptor class: ".concat(paramContext);; paramContext = new String("Failed to load module descriptor class: "))
    {
      Log.e("DynamiteModule", paramContext);
      break;
    }
  }
  
  public static int zzab(Context paramContext, String paramString)
  {
    return zzc(paramContext, paramString, false);
  }
  
  private static zzsu zzac(Context paramContext, String paramString)
  {
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {}
    for (paramString = "Selected local version of ".concat(paramString);; paramString = new String("Selected local version of "))
    {
      Log.i("DynamiteModule", paramString);
      return new zzsu(paramContext.getApplicationContext());
    }
  }
  
  public static int zzc(Context paramContext, String paramString, boolean paramBoolean)
  {
    zzsv localzzsv = zzcv(paramContext);
    if (localzzsv == null) {
      return 0;
    }
    try
    {
      int i = localzzsv.zza(zze.zzac(paramContext), paramString, paramBoolean);
      return i;
    }
    catch (RemoteException paramContext)
    {
      paramContext = String.valueOf(paramContext.getMessage());
      if (paramContext.length() == 0) {}
    }
    for (paramContext = "Failed to retrieve remote module version: ".concat(paramContext);; paramContext = new String("Failed to retrieve remote module version: "))
    {
      Log.w("DynamiteModule", paramContext);
      return 0;
    }
  }
  
  /* Error */
  private static zzsv zzcv(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 277	com/google/android/gms/internal/zzsu:Ow	Lcom/google/android/gms/internal/zzsv;
    //   6: ifnull +12 -> 18
    //   9: getstatic 277	com/google/android/gms/internal/zzsu:Ow	Lcom/google/android/gms/internal/zzsv;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: invokestatic 283	com/google/android/gms/common/zzc:zzapd	()Lcom/google/android/gms/common/zzc;
    //   21: aload_0
    //   22: invokevirtual 287	com/google/android/gms/common/zzc:isGooglePlayServicesAvailable	(Landroid/content/Context;)I
    //   25: ifeq +8 -> 33
    //   28: ldc 2
    //   30: monitorexit
    //   31: aconst_null
    //   32: areturn
    //   33: aload_0
    //   34: ldc_w 289
    //   37: iconst_3
    //   38: invokevirtual 293	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   41: invokevirtual 213	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   44: ldc_w 295
    //   47: invokevirtual 223	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   50: invokevirtual 299	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   53: checkcast 301	android/os/IBinder
    //   56: invokestatic 307	com/google/android/gms/internal/zzsv$zza:zzff	(Landroid/os/IBinder;)Lcom/google/android/gms/internal/zzsv;
    //   59: astore_0
    //   60: aload_0
    //   61: ifnull +49 -> 110
    //   64: aload_0
    //   65: putstatic 277	com/google/android/gms/internal/zzsu:Ow	Lcom/google/android/gms/internal/zzsv;
    //   68: ldc 2
    //   70: monitorexit
    //   71: aload_0
    //   72: areturn
    //   73: astore_0
    //   74: ldc 2
    //   76: monitorexit
    //   77: aload_0
    //   78: athrow
    //   79: astore_0
    //   80: aload_0
    //   81: invokevirtual 260	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   84: invokestatic 96	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   87: astore_0
    //   88: aload_0
    //   89: invokevirtual 100	java/lang/String:length	()I
    //   92: ifeq +23 -> 115
    //   95: ldc_w 309
    //   98: aload_0
    //   99: invokevirtual 154	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   102: astore_0
    //   103: ldc 88
    //   105: aload_0
    //   106: invokestatic 251	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   109: pop
    //   110: ldc 2
    //   112: monitorexit
    //   113: aconst_null
    //   114: areturn
    //   115: new 92	java/lang/String
    //   118: dup
    //   119: ldc_w 309
    //   122: invokespecial 161	java/lang/String:<init>	(Ljava/lang/String;)V
    //   125: astore_0
    //   126: goto -23 -> 103
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	129	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	16	73	finally
    //   18	31	73	finally
    //   33	60	73	finally
    //   64	68	73	finally
    //   68	71	73	finally
    //   74	77	73	finally
    //   80	103	73	finally
    //   103	110	73	finally
    //   110	113	73	finally
    //   115	126	73	finally
    //   33	60	79	java/lang/Exception
    //   64	68	79	java/lang/Exception
  }
  
  public Context zzbdy()
  {
    return this.OD;
  }
  
  public IBinder zzjd(String paramString)
    throws zzsu.zza
  {
    try
    {
      IBinder localIBinder = (IBinder)this.OD.getClassLoader().loadClass(paramString).newInstance();
      return localIBinder;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      paramString = String.valueOf(paramString);
      if (paramString.length() != 0) {}
      for (paramString = "Failed to instantiate module class: ".concat(paramString);; paramString = new String("Failed to instantiate module class: ")) {
        throw new zza(paramString, localClassNotFoundException, null);
      }
    }
    catch (InstantiationException localInstantiationException)
    {
      for (;;) {}
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;) {}
    }
  }
  
  public static class zza
    extends Exception
  {
    private zza(String paramString)
    {
      super();
    }
    
    private zza(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  public static abstract interface zzb
  {
    public abstract zzb zza(Context paramContext, String paramString, zza paramzza);
    
    public static abstract interface zza
    {
      public abstract int zzaa(Context paramContext, String paramString);
      
      public abstract int zzc(Context paramContext, String paramString, boolean paramBoolean);
    }
    
    public static class zzb
    {
      public int OF = 0;
      public int OG = 0;
      public int OH = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */