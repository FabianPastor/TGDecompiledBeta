package com.google.android.gms.dynamite;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.ObjectWrapper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.concurrent.GuardedBy;

public final class DynamiteModule
{
  public static final VersionPolicy PREFER_HIGHEST_OR_LOCAL_VERSION = new zzd();
  public static final VersionPolicy PREFER_HIGHEST_OR_LOCAL_VERSION_NO_FORCE_STAGING = new zze();
  public static final VersionPolicy PREFER_HIGHEST_OR_REMOTE_VERSION = new zzf();
  public static final VersionPolicy PREFER_HIGHEST_OR_REMOTE_VERSION_NO_FORCE_STAGING = new zzg();
  public static final VersionPolicy PREFER_LOCAL;
  public static final VersionPolicy PREFER_REMOTE;
  @GuardedBy("DynamiteModule.class")
  private static Boolean zzabr;
  @GuardedBy("DynamiteModule.class")
  private static IDynamiteLoader zzabs;
  @GuardedBy("DynamiteModule.class")
  private static IDynamiteLoaderV2 zzabt;
  @GuardedBy("DynamiteModule.class")
  private static String zzabu;
  private static final ThreadLocal<zza> zzabv = new ThreadLocal();
  private static final DynamiteModule.VersionPolicy.IVersions zzabw = new zza();
  private final Context zzabx;
  
  static
  {
    PREFER_REMOTE = new zzb();
    PREFER_LOCAL = new zzc();
  }
  
  private DynamiteModule(Context paramContext)
  {
    this.zzabx = ((Context)Preconditions.checkNotNull(paramContext));
  }
  
  public static int getLocalVersion(Context paramContext, String paramString)
  {
    for (;;)
    {
      try
      {
        localObject = paramContext.getApplicationContext().getClassLoader();
        i = String.valueOf(paramString).length();
        paramContext = new java/lang/StringBuilder;
        paramContext.<init>(i + 61);
        localObject = ((ClassLoader)localObject).loadClass("com.google.android.gms.dynamite.descriptors." + paramString + ".ModuleDescriptor");
        paramContext = ((Class)localObject).getDeclaredField("MODULE_ID");
        localObject = ((Class)localObject).getDeclaredField("MODULE_VERSION");
        if (paramContext.get(null).equals(paramString)) {
          continue;
        }
        localObject = String.valueOf(paramContext.get(null));
        i = String.valueOf(localObject).length();
        int j = String.valueOf(paramString).length();
        paramContext = new java/lang/StringBuilder;
        paramContext.<init>(i + 51 + j);
        Log.e("DynamiteModule", "Module descriptor id '" + (String)localObject + "' didn't match expected id '" + paramString + "'");
        i = 0;
      }
      catch (ClassNotFoundException paramContext)
      {
        Object localObject;
        Log.w("DynamiteModule", String.valueOf(paramString).length() + 45 + "Local module descriptor class for " + paramString + " not found.");
        int i = 0;
        continue;
      }
      catch (Exception paramContext)
      {
        paramContext = String.valueOf(paramContext.getMessage());
        if (paramContext.length() == 0) {
          break;
        }
      }
      return i;
      i = ((Field)localObject).getInt(null);
    }
    for (paramContext = "Failed to load module descriptor class: ".concat(paramContext);; paramContext = new String("Failed to load module descriptor class: "))
    {
      Log.e("DynamiteModule", paramContext);
      break;
    }
  }
  
  public static Uri getQueryUri(String paramString, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (String str = "api_force_staging";; str = "api") {
      return Uri.parse(String.valueOf(str).length() + 42 + String.valueOf(paramString).length() + "content://com.google.android.gms.chimera/" + str + "/" + paramString);
    }
  }
  
  /* Error */
  public static int getRemoteVersion(Context paramContext, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 234	com/google/android/gms/dynamite/DynamiteModule:zzabr	Ljava/lang/Boolean;
    //   6: astore_3
    //   7: aload_3
    //   8: astore 4
    //   10: aload_3
    //   11: ifnonnull +65 -> 76
    //   14: aload_0
    //   15: invokevirtual 117	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   18: invokevirtual 121	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   21: ldc 6
    //   23: invokevirtual 237	java/lang/Class:getName	()Ljava/lang/String;
    //   26: invokevirtual 154	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   29: astore_3
    //   30: aload_3
    //   31: ldc -17
    //   33: invokevirtual 162	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   36: astore 4
    //   38: aload_3
    //   39: monitorenter
    //   40: aload 4
    //   42: aconst_null
    //   43: invokevirtual 169	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   46: checkcast 150	java/lang/ClassLoader
    //   49: astore 5
    //   51: aload 5
    //   53: ifnull +58 -> 111
    //   56: aload 5
    //   58: invokestatic 242	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   61: if_acmpne +37 -> 98
    //   64: getstatic 247	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   67: astore 4
    //   69: aload_3
    //   70: monitorexit
    //   71: aload 4
    //   73: putstatic 234	com/google/android/gms/dynamite/DynamiteModule:zzabr	Ljava/lang/Boolean;
    //   76: ldc 2
    //   78: monitorexit
    //   79: aload 4
    //   81: invokevirtual 251	java/lang/Boolean:booleanValue	()Z
    //   84: ifeq +282 -> 366
    //   87: aload_0
    //   88: aload_1
    //   89: iload_2
    //   90: invokestatic 253	com/google/android/gms/dynamite/DynamiteModule:zzb	(Landroid/content/Context;Ljava/lang/String;Z)I
    //   93: istore 6
    //   95: iload 6
    //   97: ireturn
    //   98: aload 5
    //   100: invokestatic 256	com/google/android/gms/dynamite/DynamiteModule:zza	(Ljava/lang/ClassLoader;)V
    //   103: getstatic 259	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   106: astore 4
    //   108: goto -39 -> 69
    //   111: ldc_w 261
    //   114: aload_0
    //   115: invokevirtual 117	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   118: invokevirtual 264	android/content/Context:getPackageName	()Ljava/lang/String;
    //   121: invokevirtual 265	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   124: ifeq +20 -> 144
    //   127: aload 4
    //   129: aconst_null
    //   130: invokestatic 242	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   133: invokevirtual 269	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   136: getstatic 247	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   139: astore 4
    //   141: goto -72 -> 69
    //   144: aload_0
    //   145: aload_1
    //   146: iload_2
    //   147: invokestatic 253	com/google/android/gms/dynamite/DynamiteModule:zzb	(Landroid/content/Context;Ljava/lang/String;Z)I
    //   150: istore 6
    //   152: getstatic 271	com/google/android/gms/dynamite/DynamiteModule:zzabu	Ljava/lang/String;
    //   155: ifnull +16 -> 171
    //   158: getstatic 271	com/google/android/gms/dynamite/DynamiteModule:zzabu	Ljava/lang/String;
    //   161: invokevirtual 274	java/lang/String:isEmpty	()Z
    //   164: istore 7
    //   166: iload 7
    //   168: ifeq +17 -> 185
    //   171: aload_3
    //   172: monitorexit
    //   173: ldc 2
    //   175: monitorexit
    //   176: goto -81 -> 95
    //   179: astore_0
    //   180: ldc 2
    //   182: monitorexit
    //   183: aload_0
    //   184: athrow
    //   185: new 276	com/google/android/gms/dynamite/zzh
    //   188: astore 5
    //   190: aload 5
    //   192: getstatic 271	com/google/android/gms/dynamite/DynamiteModule:zzabu	Ljava/lang/String;
    //   195: invokestatic 242	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   198: invokespecial 279	com/google/android/gms/dynamite/zzh:<init>	(Ljava/lang/String;Ljava/lang/ClassLoader;)V
    //   201: aload 5
    //   203: invokestatic 256	com/google/android/gms/dynamite/DynamiteModule:zza	(Ljava/lang/ClassLoader;)V
    //   206: aload 4
    //   208: aconst_null
    //   209: aload 5
    //   211: invokevirtual 269	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   214: getstatic 259	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   217: putstatic 234	com/google/android/gms/dynamite/DynamiteModule:zzabr	Ljava/lang/Boolean;
    //   220: aload_3
    //   221: monitorexit
    //   222: ldc 2
    //   224: monitorexit
    //   225: goto -130 -> 95
    //   228: astore 5
    //   230: aload 4
    //   232: aconst_null
    //   233: invokestatic 242	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   236: invokevirtual 269	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   239: getstatic 247	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   242: astore 4
    //   244: goto -175 -> 69
    //   247: astore 4
    //   249: aload_3
    //   250: monitorexit
    //   251: aload 4
    //   253: athrow
    //   254: astore 4
    //   256: aload 4
    //   258: invokestatic 127	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   261: astore_3
    //   262: aload_3
    //   263: invokestatic 127	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   266: invokevirtual 131	java/lang/String:length	()I
    //   269: istore 6
    //   271: new 133	java/lang/StringBuilder
    //   274: astore 4
    //   276: aload 4
    //   278: iload 6
    //   280: bipush 30
    //   282: iadd
    //   283: invokespecial 136	java/lang/StringBuilder:<init>	(I)V
    //   286: ldc -81
    //   288: aload 4
    //   290: ldc_w 281
    //   293: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: aload_3
    //   297: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: invokevirtual 148	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: invokestatic 198	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   306: pop
    //   307: getstatic 247	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   310: astore 4
    //   312: goto -241 -> 71
    //   315: astore_0
    //   316: aload_0
    //   317: invokevirtual 282	com/google/android/gms/dynamite/DynamiteModule$LoadingException:getMessage	()Ljava/lang/String;
    //   320: invokestatic 127	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   323: astore_0
    //   324: aload_0
    //   325: invokevirtual 131	java/lang/String:length	()I
    //   328: ifeq +24 -> 352
    //   331: ldc_w 284
    //   334: aload_0
    //   335: invokevirtual 207	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   338: astore_0
    //   339: ldc -81
    //   341: aload_0
    //   342: invokestatic 198	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   345: pop
    //   346: iconst_0
    //   347: istore 6
    //   349: goto -254 -> 95
    //   352: new 123	java/lang/String
    //   355: dup
    //   356: ldc_w 284
    //   359: invokespecial 210	java/lang/String:<init>	(Ljava/lang/String;)V
    //   362: astore_0
    //   363: goto -24 -> 339
    //   366: aload_0
    //   367: aload_1
    //   368: iload_2
    //   369: invokestatic 286	com/google/android/gms/dynamite/DynamiteModule:zza	(Landroid/content/Context;Ljava/lang/String;Z)I
    //   372: istore 6
    //   374: goto -279 -> 95
    //   377: astore 4
    //   379: goto -276 -> 103
    //   382: astore 4
    //   384: goto -128 -> 256
    //   387: astore 4
    //   389: goto -133 -> 256
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	392	0	paramContext	Context
    //   0	392	1	paramString	String
    //   0	392	2	paramBoolean	boolean
    //   6	291	3	localObject1	Object
    //   8	235	4	localObject2	Object
    //   247	5	4	localObject3	Object
    //   254	3	4	localClassNotFoundException	ClassNotFoundException
    //   274	37	4	localObject4	Object
    //   377	1	4	localLoadingException1	LoadingException
    //   382	1	4	localNoSuchFieldException	NoSuchFieldException
    //   387	1	4	localIllegalAccessException	IllegalAccessException
    //   49	161	5	localObject5	Object
    //   228	1	5	localLoadingException2	LoadingException
    //   93	280	6	i	int
    //   164	3	7	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   3	7	179	finally
    //   14	40	179	finally
    //   71	76	179	finally
    //   76	79	179	finally
    //   173	176	179	finally
    //   180	183	179	finally
    //   222	225	179	finally
    //   251	254	179	finally
    //   256	312	179	finally
    //   144	166	228	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   185	220	228	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   40	51	247	finally
    //   56	69	247	finally
    //   69	71	247	finally
    //   98	103	247	finally
    //   103	108	247	finally
    //   111	141	247	finally
    //   144	166	247	finally
    //   171	173	247	finally
    //   185	220	247	finally
    //   220	222	247	finally
    //   230	244	247	finally
    //   249	251	247	finally
    //   14	40	254	java/lang/ClassNotFoundException
    //   251	254	254	java/lang/ClassNotFoundException
    //   87	95	315	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   98	103	377	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   14	40	382	java/lang/NoSuchFieldException
    //   251	254	382	java/lang/NoSuchFieldException
    //   14	40	387	java/lang/IllegalAccessException
    //   251	254	387	java/lang/IllegalAccessException
  }
  
  public static DynamiteModule load(Context paramContext, VersionPolicy paramVersionPolicy, String paramString)
    throws DynamiteModule.LoadingException
  {
    zza localzza1 = (zza)zzabv.get();
    zza localzza2 = new zza(null);
    zzabv.set(localzza2);
    DynamiteModule.VersionPolicy.SelectionResult localSelectionResult;
    Object localObject;
    try
    {
      localSelectionResult = paramVersionPolicy.selectModule(paramContext, paramString, zzabw);
      int i = localSelectionResult.localVersion;
      int j = localSelectionResult.remoteVersion;
      int k = String.valueOf(paramString).length();
      m = String.valueOf(paramString).length();
      localObject = new java/lang/StringBuilder;
      ((StringBuilder)localObject).<init>(k + 68 + m);
      Log.i("DynamiteModule", "Considering local module " + paramString + ":" + i + " and remote module " + paramString + ":" + j);
      if ((localSelectionResult.selection == 0) || ((localSelectionResult.selection == -1) && (localSelectionResult.localVersion == 0)) || ((localSelectionResult.selection == 1) && (localSelectionResult.remoteVersion == 0)))
      {
        paramContext = new com/google/android/gms/dynamite/DynamiteModule$LoadingException;
        m = localSelectionResult.localVersion;
        k = localSelectionResult.remoteVersion;
        paramVersionPolicy = new java/lang/StringBuilder;
        paramVersionPolicy.<init>(91);
        paramContext.<init>("No acceptable module found. Local version is " + m + " and remote version is " + k + ".", null);
        throw paramContext;
      }
    }
    finally
    {
      if (localzza2.zzaby != null) {
        localzza2.zzaby.close();
      }
      zzabv.set(localzza1);
    }
    if (localSelectionResult.selection == -1)
    {
      paramContext = zzd(paramContext, paramString);
      if (localzza2.zzaby != null) {
        localzza2.zzaby.close();
      }
      zzabv.set(localzza1);
    }
    for (;;)
    {
      return paramContext;
      m = localSelectionResult.selection;
      if (m == 1) {
        try
        {
          localObject = zza(paramContext, paramString, localSelectionResult.remoteVersion);
          if (localzza2.zzaby != null) {
            localzza2.zzaby.close();
          }
          zzabv.set(localzza1);
          paramContext = (Context)localObject;
        }
        catch (LoadingException localLoadingException)
        {
          localObject = String.valueOf(localLoadingException.getMessage());
          if (((String)localObject).length() != 0) {}
          for (localObject = "Failed to load remote module: ".concat((String)localObject);; localObject = new String("Failed to load remote module: "))
          {
            Log.w("DynamiteModule", (String)localObject);
            if (localSelectionResult.localVersion == 0) {
              break label507;
            }
            localObject = new com/google/android/gms/dynamite/DynamiteModule$zzb;
            ((zzb)localObject).<init>(localSelectionResult.localVersion, 0);
            if (paramVersionPolicy.selectModule(paramContext, paramString, (DynamiteModule.VersionPolicy.IVersions)localObject).selection != -1) {
              break label507;
            }
            paramContext = zzd(paramContext, paramString);
            if (localzza2.zzaby != null) {
              localzza2.zzaby.close();
            }
            zzabv.set(localzza1);
            break;
          }
          label507:
          paramContext = new com/google/android/gms/dynamite/DynamiteModule$LoadingException;
          paramContext.<init>("Remote load failed. No local fallback found.", localLoadingException, null);
          throw paramContext;
        }
      }
    }
    paramContext = new com/google/android/gms/dynamite/DynamiteModule$LoadingException;
    int m = localSelectionResult.selection;
    paramVersionPolicy = new java/lang/StringBuilder;
    paramVersionPolicy.<init>(47);
    paramContext.<init>("VersionPolicy returned invalid code:" + m, null);
    throw paramContext;
  }
  
  public static Cursor queryForDynamiteModule(Context paramContext, String paramString, boolean paramBoolean)
  {
    return paramContext.getContentResolver().query(getQueryUri(paramString, paramBoolean), null, null, null, null);
  }
  
  private static int zza(Context paramContext, String paramString, boolean paramBoolean)
  {
    IDynamiteLoader localIDynamiteLoader = zzg(paramContext);
    int i;
    if (localIDynamiteLoader == null) {
      i = 0;
    }
    for (;;)
    {
      return i;
      try
      {
        i = localIDynamiteLoader.getModuleVersion2(ObjectWrapper.wrap(paramContext), paramString, paramBoolean);
      }
      catch (RemoteException paramContext)
      {
        paramContext = String.valueOf(paramContext.getMessage());
        if (paramContext.length() == 0) {}
      }
    }
    for (paramContext = "Failed to retrieve remote module version: ".concat(paramContext);; paramContext = new String("Failed to retrieve remote module version: "))
    {
      Log.w("DynamiteModule", paramContext);
      i = 0;
      break;
    }
  }
  
  private static Context zza(Context paramContext, String paramString, int paramInt, Cursor paramCursor, IDynamiteLoaderV2 paramIDynamiteLoaderV2)
  {
    try
    {
      paramContext = (Context)ObjectWrapper.unwrap(paramIDynamiteLoaderV2.loadModule2(ObjectWrapper.wrap(paramContext), paramString, paramInt, ObjectWrapper.wrap(paramCursor)));
      return paramContext;
    }
    catch (Exception paramContext)
    {
      paramContext = String.valueOf(paramContext.toString());
      if (paramContext.length() == 0) {}
    }
    for (paramContext = "Failed to load DynamiteLoader: ".concat(paramContext);; paramContext = new String("Failed to load DynamiteLoader: "))
    {
      Log.e("DynamiteModule", paramContext);
      paramContext = null;
      break;
    }
  }
  
  private static DynamiteModule zza(Context paramContext, String paramString, int paramInt)
    throws DynamiteModule.LoadingException
  {
    Boolean localBoolean;
    try
    {
      localBoolean = zzabr;
      if (localBoolean == null) {
        throw new LoadingException("Failed to determine which loading route to use.", null);
      }
    }
    finally {}
    if (localBoolean.booleanValue()) {}
    for (paramContext = zzc(paramContext, paramString, paramInt);; paramContext = zzb(paramContext, paramString, paramInt)) {
      return paramContext;
    }
  }
  
  @GuardedBy("DynamiteModule.class")
  private static void zza(ClassLoader paramClassLoader)
    throws DynamiteModule.LoadingException
  {
    try
    {
      zzabt = IDynamiteLoaderV2.Stub.asInterface((IBinder)paramClassLoader.loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2").getConstructor(new Class[0]).newInstance(new Object[0]));
      return;
    }
    catch (ClassNotFoundException paramClassLoader)
    {
      throw new LoadingException("Failed to instantiate dynamite loader", paramClassLoader, null);
    }
    catch (InstantiationException paramClassLoader)
    {
      for (;;) {}
    }
    catch (IllegalAccessException paramClassLoader)
    {
      for (;;) {}
    }
    catch (NoSuchMethodException paramClassLoader)
    {
      for (;;) {}
    }
    catch (InvocationTargetException paramClassLoader)
    {
      for (;;) {}
    }
  }
  
  /* Error */
  private static int zzb(Context paramContext, String paramString, boolean paramBoolean)
    throws DynamiteModule.LoadingException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iload_2
    //   3: invokestatic 448	com/google/android/gms/dynamite/DynamiteModule:queryForDynamiteModule	(Landroid/content/Context;Ljava/lang/String;Z)Landroid/database/Cursor;
    //   6: astore_0
    //   7: aload_0
    //   8: ifnull +14 -> 22
    //   11: aload_0
    //   12: astore_1
    //   13: aload_0
    //   14: invokeinterface 451 1 0
    //   19: ifne +61 -> 80
    //   22: aload_0
    //   23: astore_1
    //   24: ldc -81
    //   26: ldc_w 453
    //   29: invokestatic 198	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   32: pop
    //   33: aload_0
    //   34: astore_1
    //   35: new 9	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   38: astore_3
    //   39: aload_0
    //   40: astore_1
    //   41: aload_3
    //   42: ldc_w 455
    //   45: aconst_null
    //   46: invokespecial 332	com/google/android/gms/dynamite/DynamiteModule$LoadingException:<init>	(Ljava/lang/String;Lcom/google/android/gms/dynamite/zza;)V
    //   49: aload_0
    //   50: astore_1
    //   51: aload_3
    //   52: athrow
    //   53: astore_3
    //   54: aload_0
    //   55: astore_1
    //   56: aload_3
    //   57: instanceof 9
    //   60: ifeq +119 -> 179
    //   63: aload_0
    //   64: astore_1
    //   65: aload_3
    //   66: athrow
    //   67: astore_0
    //   68: aload_1
    //   69: ifnull +9 -> 78
    //   72: aload_1
    //   73: invokeinterface 341 1 0
    //   78: aload_0
    //   79: athrow
    //   80: aload_0
    //   81: astore_1
    //   82: aload_0
    //   83: iconst_0
    //   84: invokeinterface 458 2 0
    //   89: istore 4
    //   91: aload_0
    //   92: astore_3
    //   93: iload 4
    //   95: ifle +63 -> 158
    //   98: aload_0
    //   99: astore_1
    //   100: ldc 2
    //   102: monitorenter
    //   103: aload_0
    //   104: iconst_2
    //   105: invokeinterface 462 2 0
    //   110: putstatic 271	com/google/android/gms/dynamite/DynamiteModule:zzabu	Ljava/lang/String;
    //   113: ldc 2
    //   115: monitorexit
    //   116: aload_0
    //   117: astore_1
    //   118: getstatic 59	com/google/android/gms/dynamite/DynamiteModule:zzabv	Ljava/lang/ThreadLocal;
    //   121: invokevirtual 291	java/lang/ThreadLocal:get	()Ljava/lang/Object;
    //   124: checkcast 21	com/google/android/gms/dynamite/DynamiteModule$zza
    //   127: astore 5
    //   129: aload_0
    //   130: astore_3
    //   131: aload 5
    //   133: ifnull +25 -> 158
    //   136: aload_0
    //   137: astore_1
    //   138: aload_0
    //   139: astore_3
    //   140: aload 5
    //   142: getfield 336	com/google/android/gms/dynamite/DynamiteModule$zza:zzaby	Landroid/database/Cursor;
    //   145: ifnonnull +13 -> 158
    //   148: aload_0
    //   149: astore_1
    //   150: aload 5
    //   152: aload_0
    //   153: putfield 336	com/google/android/gms/dynamite/DynamiteModule$zza:zzaby	Landroid/database/Cursor;
    //   156: aconst_null
    //   157: astore_3
    //   158: aload_3
    //   159: ifnull +9 -> 168
    //   162: aload_3
    //   163: invokeinterface 341 1 0
    //   168: iload 4
    //   170: ireturn
    //   171: astore_3
    //   172: ldc 2
    //   174: monitorexit
    //   175: aload_0
    //   176: astore_1
    //   177: aload_3
    //   178: athrow
    //   179: aload_0
    //   180: astore_1
    //   181: new 9	com/google/android/gms/dynamite/DynamiteModule$LoadingException
    //   184: astore 5
    //   186: aload_0
    //   187: astore_1
    //   188: aload 5
    //   190: ldc_w 464
    //   193: aload_3
    //   194: aconst_null
    //   195: invokespecial 358	com/google/android/gms/dynamite/DynamiteModule$LoadingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;Lcom/google/android/gms/dynamite/zza;)V
    //   198: aload_0
    //   199: astore_1
    //   200: aload 5
    //   202: athrow
    //   203: astore_0
    //   204: aconst_null
    //   205: astore_1
    //   206: goto -138 -> 68
    //   209: astore_3
    //   210: aconst_null
    //   211: astore_0
    //   212: goto -158 -> 54
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	215	0	paramContext	Context
    //   0	215	1	paramString	String
    //   0	215	2	paramBoolean	boolean
    //   38	14	3	localLoadingException	LoadingException
    //   53	13	3	localException1	Exception
    //   92	71	3	localContext	Context
    //   171	23	3	localThrowable	Throwable
    //   209	1	3	localException2	Exception
    //   89	80	4	i	int
    //   127	74	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	22	53	java/lang/Exception
    //   24	33	53	java/lang/Exception
    //   35	39	53	java/lang/Exception
    //   41	49	53	java/lang/Exception
    //   51	53	53	java/lang/Exception
    //   82	91	53	java/lang/Exception
    //   100	103	53	java/lang/Exception
    //   118	129	53	java/lang/Exception
    //   140	148	53	java/lang/Exception
    //   150	156	53	java/lang/Exception
    //   177	179	53	java/lang/Exception
    //   13	22	67	finally
    //   24	33	67	finally
    //   35	39	67	finally
    //   41	49	67	finally
    //   51	53	67	finally
    //   56	63	67	finally
    //   65	67	67	finally
    //   82	91	67	finally
    //   100	103	67	finally
    //   118	129	67	finally
    //   140	148	67	finally
    //   150	156	67	finally
    //   177	179	67	finally
    //   181	186	67	finally
    //   188	198	67	finally
    //   200	203	67	finally
    //   103	116	171	finally
    //   172	175	171	finally
    //   0	7	203	finally
    //   0	7	209	java/lang/Exception
  }
  
  private static DynamiteModule zzb(Context paramContext, String paramString, int paramInt)
    throws DynamiteModule.LoadingException
  {
    Log.i("DynamiteModule", String.valueOf(paramString).length() + 51 + "Selected remote version of " + paramString + ", version >= " + paramInt);
    IDynamiteLoader localIDynamiteLoader = zzg(paramContext);
    if (localIDynamiteLoader == null) {
      throw new LoadingException("Failed to create IDynamiteLoader.", null);
    }
    try
    {
      paramContext = localIDynamiteLoader.createModuleContext(ObjectWrapper.wrap(paramContext), paramString, paramInt);
      if (ObjectWrapper.unwrap(paramContext) == null) {
        throw new LoadingException("Failed to load remote module.", null);
      }
    }
    catch (RemoteException paramContext)
    {
      throw new LoadingException("Failed to load remote module.", paramContext, null);
    }
    return new DynamiteModule((Context)ObjectWrapper.unwrap(paramContext));
  }
  
  private static DynamiteModule zzc(Context paramContext, String paramString, int paramInt)
    throws DynamiteModule.LoadingException
  {
    Log.i("DynamiteModule", String.valueOf(paramString).length() + 51 + "Selected remote version of " + paramString + ", version >= " + paramInt);
    IDynamiteLoaderV2 localIDynamiteLoaderV2;
    try
    {
      localIDynamiteLoaderV2 = zzabt;
      if (localIDynamiteLoaderV2 == null) {
        throw new LoadingException("DynamiteLoaderV2 was not cached.", null);
      }
    }
    finally {}
    zza localzza = (zza)zzabv.get();
    if ((localzza == null) || (localzza.zzaby == null)) {
      throw new LoadingException("No result cursor", null);
    }
    paramContext = zza(paramContext.getApplicationContext(), paramString, paramInt, localzza.zzaby, localIDynamiteLoaderV2);
    if (paramContext == null) {
      throw new LoadingException("Failed to get module context", null);
    }
    return new DynamiteModule(paramContext);
  }
  
  private static DynamiteModule zzd(Context paramContext, String paramString)
  {
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {}
    for (paramString = "Selected local version of ".concat(paramString);; paramString = new String("Selected local version of "))
    {
      Log.i("DynamiteModule", paramString);
      return new DynamiteModule(paramContext.getApplicationContext());
    }
  }
  
  /* Error */
  private static IDynamiteLoader zzg(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 490	com/google/android/gms/dynamite/DynamiteModule:zzabs	Lcom/google/android/gms/dynamite/IDynamiteLoader;
    //   6: ifnull +12 -> 18
    //   9: getstatic 490	com/google/android/gms/dynamite/DynamiteModule:zzabs	Lcom/google/android/gms/dynamite/IDynamiteLoader;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: invokestatic 496	com/google/android/gms/common/GoogleApiAvailabilityLight:getInstance	()Lcom/google/android/gms/common/GoogleApiAvailabilityLight;
    //   21: aload_0
    //   22: invokevirtual 500	com/google/android/gms/common/GoogleApiAvailabilityLight:isGooglePlayServicesAvailable	(Landroid/content/Context;)I
    //   25: ifeq +11 -> 36
    //   28: ldc 2
    //   30: monitorexit
    //   31: aconst_null
    //   32: astore_0
    //   33: goto -17 -> 16
    //   36: aload_0
    //   37: ldc_w 261
    //   40: iconst_3
    //   41: invokevirtual 504	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   44: invokevirtual 121	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   47: ldc_w 506
    //   50: invokevirtual 154	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   53: invokevirtual 508	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   56: checkcast 435	android/os/IBinder
    //   59: invokestatic 513	com/google/android/gms/dynamite/IDynamiteLoader$Stub:asInterface	(Landroid/os/IBinder;)Lcom/google/android/gms/dynamite/IDynamiteLoader;
    //   62: astore_0
    //   63: aload_0
    //   64: ifnull +50 -> 114
    //   67: aload_0
    //   68: putstatic 490	com/google/android/gms/dynamite/DynamiteModule:zzabs	Lcom/google/android/gms/dynamite/IDynamiteLoader;
    //   71: ldc 2
    //   73: monitorexit
    //   74: goto -58 -> 16
    //   77: astore_0
    //   78: ldc 2
    //   80: monitorexit
    //   81: aload_0
    //   82: athrow
    //   83: astore_0
    //   84: aload_0
    //   85: invokevirtual 201	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   88: invokestatic 127	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   91: astore_0
    //   92: aload_0
    //   93: invokevirtual 131	java/lang/String:length	()I
    //   96: ifeq +26 -> 122
    //   99: ldc_w 515
    //   102: aload_0
    //   103: invokevirtual 207	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   106: astore_0
    //   107: ldc -81
    //   109: aload_0
    //   110: invokestatic 187	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   113: pop
    //   114: ldc 2
    //   116: monitorexit
    //   117: aconst_null
    //   118: astore_0
    //   119: goto -103 -> 16
    //   122: new 123	java/lang/String
    //   125: dup
    //   126: ldc_w 515
    //   129: invokespecial 210	java/lang/String:<init>	(Ljava/lang/String;)V
    //   132: astore_0
    //   133: goto -26 -> 107
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	16	77	finally
    //   18	31	77	finally
    //   36	63	77	finally
    //   67	71	77	finally
    //   71	74	77	finally
    //   78	81	77	finally
    //   84	107	77	finally
    //   107	114	77	finally
    //   114	117	77	finally
    //   122	133	77	finally
    //   36	63	83	java/lang/Exception
    //   67	71	83	java/lang/Exception
  }
  
  public final Context getModuleContext()
  {
    return this.zzabx;
  }
  
  public final IBinder instantiate(String paramString)
    throws DynamiteModule.LoadingException
  {
    try
    {
      IBinder localIBinder = (IBinder)this.zzabx.getClassLoader().loadClass(paramString).newInstance();
      return localIBinder;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      paramString = String.valueOf(paramString);
      if (paramString.length() != 0) {}
      for (paramString = "Failed to instantiate module class: ".concat(paramString);; paramString = new String("Failed to instantiate module class: ")) {
        throw new LoadingException(paramString, localClassNotFoundException, null);
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
  
  @DynamiteApi
  public static class DynamiteLoaderClassLoader
  {
    @GuardedBy("DynamiteLoaderClassLoader.class")
    public static ClassLoader sClassLoader;
  }
  
  public static class LoadingException
    extends Exception
  {
    private LoadingException(String paramString)
    {
      super();
    }
    
    private LoadingException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  public static abstract interface VersionPolicy
  {
    public abstract SelectionResult selectModule(Context paramContext, String paramString, IVersions paramIVersions)
      throws DynamiteModule.LoadingException;
    
    public static abstract interface IVersions
    {
      public abstract int getLocalVersion(Context paramContext, String paramString);
      
      public abstract int getRemoteVersion(Context paramContext, String paramString, boolean paramBoolean)
        throws DynamiteModule.LoadingException;
    }
    
    public static class SelectionResult
    {
      public int localVersion = 0;
      public int remoteVersion = 0;
      public int selection = 0;
    }
  }
  
  private static final class zza
  {
    public Cursor zzaby;
  }
  
  private static final class zzb
    implements DynamiteModule.VersionPolicy.IVersions
  {
    private final int zzabz;
    private final int zzaca;
    
    public zzb(int paramInt1, int paramInt2)
    {
      this.zzabz = paramInt1;
      this.zzaca = 0;
    }
    
    public final int getLocalVersion(Context paramContext, String paramString)
    {
      return this.zzabz;
    }
    
    public final int getRemoteVersion(Context paramContext, String paramString, boolean paramBoolean)
    {
      return 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/DynamiteModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */