package com.google.android.gms.common;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageInstaller.SessionInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.google.android.gms.R.string;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.common.util.zzl;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzy;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class zze
{
  @Deprecated
  public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
  @Deprecated
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = ;
  public static final String GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending";
  public static boolean uX = false;
  public static boolean uY = false;
  static boolean uZ = false;
  private static String va = null;
  private static int vb = 0;
  private static boolean vc = false;
  static final AtomicBoolean vd = new AtomicBoolean();
  private static final AtomicBoolean ve = new AtomicBoolean();
  
  @Deprecated
  public static PendingIntent getErrorPendingIntent(int paramInt1, Context paramContext, int paramInt2)
  {
    return zzc.zzapd().getErrorResolutionPendingIntent(paramContext, paramInt1, paramInt2);
  }
  
  @Deprecated
  public static String getErrorString(int paramInt)
  {
    return ConnectionResult.getStatusString(paramInt);
  }
  
  @Deprecated
  public static String getOpenSourceSoftwareLicenseInfo(Context paramContext)
  {
    Object localObject = new Uri.Builder().scheme("android.resource").authority("com.google.android.gms").appendPath("raw").appendPath("oss_notice").build();
    try
    {
      InputStream localInputStream = paramContext.getContentResolver().openInputStream((Uri)localObject);
      try
      {
        paramContext = new Scanner(localInputStream).useDelimiter("\\A").next();
        localObject = paramContext;
        if (localInputStream != null)
        {
          localInputStream.close();
          return paramContext;
        }
      }
      catch (NoSuchElementException paramContext)
      {
        paramContext = paramContext;
        if (localInputStream == null) {
          break label97;
        }
        localInputStream.close();
        break label97;
      }
      finally
      {
        paramContext = finally;
        if (localInputStream != null) {
          localInputStream.close();
        }
        throw paramContext;
      }
      return (String)localObject;
    }
    catch (Exception paramContext)
    {
      localObject = null;
    }
    label97:
    return null;
  }
  
  public static Context getRemoteContext(Context paramContext)
  {
    try
    {
      paramContext = paramContext.createPackageContext("com.google.android.gms", 3);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  public static Resources getRemoteResource(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getResourcesForApplication("com.google.android.gms");
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  @Deprecated
  public static int isGooglePlayServicesAvailable(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    PackageInfo localPackageInfo;
    try
    {
      paramContext.getResources().getString(R.string.common_google_play_services_unknown_issue);
      if (!"com.google.android.gms".equals(paramContext.getPackageName())) {
        zzbt(paramContext);
      }
      if (!zzi.zzcl(paramContext))
      {
        i = 1;
        localObject = null;
        if (i == 0) {}
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        try
        {
          localObject = localPackageManager.getPackageInfo("com.android.vending", 8256);
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Object localObject;
          Log.w("GooglePlayServicesUtil", "Google Play Store is missing.");
          return 9;
        }
        try
        {
          localPackageInfo = localPackageManager.getPackageInfo("com.google.android.gms", 64);
          paramContext = zzf.zzbz(paramContext);
          if (i == 0) {
            break label171;
          }
          localObject = paramContext.zza((PackageInfo)localObject, zzd.zzd.uW);
          if (localObject != null) {
            break label143;
          }
          Log.w("GooglePlayServicesUtil", "Google Play Store signature invalid.");
          return 9;
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
          return 1;
        }
        localThrowable = localThrowable;
        Log.e("GooglePlayServicesUtil", "The Google Play services resources were not found. Check your project configuration to ensure that the resources are included.");
        continue;
        i = 0;
      }
    }
    label143:
    if (paramContext.zza(localPackageInfo, new zzd.zza[] { localThrowable }) == null)
    {
      Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
      return 9;
      label171:
      if (paramContext.zza(localPackageInfo, zzd.zzd.uW) == null)
      {
        Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
        return 9;
      }
    }
    int i = zzl.zzhj(GOOGLE_PLAY_SERVICES_VERSION_CODE);
    if (zzl.zzhj(localPackageInfo.versionCode) < i)
    {
      i = GOOGLE_PLAY_SERVICES_VERSION_CODE;
      int j = localPackageInfo.versionCode;
      Log.w("GooglePlayServicesUtil", 77 + "Google Play services out of date.  Requires " + i + " but found " + j);
      return 2;
    }
    ApplicationInfo localApplicationInfo = localPackageInfo.applicationInfo;
    paramContext = localApplicationInfo;
    if (localApplicationInfo == null) {}
    try
    {
      paramContext = localPackageManager.getApplicationInfo("com.google.android.gms", 0);
      if (!paramContext.enabled) {
        return 3;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.wtf("GooglePlayServicesUtil", "Google Play services missing when getting application info.", paramContext);
      return 1;
    }
    return 0;
  }
  
  @Deprecated
  public static boolean isUserRecoverableError(int paramInt)
  {
    switch (paramInt)
    {
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    default: 
      return false;
    }
    return true;
  }
  
  private static int zzapk()
  {
    return com.google.android.gms.common.internal.zzf.BA;
  }
  
  @Deprecated
  public static boolean zzapl()
  {
    return "user".equals(Build.TYPE);
  }
  
  @Deprecated
  @TargetApi(19)
  public static boolean zzb(Context paramContext, int paramInt, String paramString)
  {
    return zzy.zzb(paramContext, paramInt, paramString);
  }
  
  @Deprecated
  public static void zzbc(Context paramContext)
    throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException
  {
    int i = zzc.zzapd().isGooglePlayServicesAvailable(paramContext);
    if (i != 0)
    {
      paramContext = zzc.zzapd().zza(paramContext, i, "e");
      Log.e("GooglePlayServicesUtil", 57 + "GooglePlayServices not available due to error " + i);
      if (paramContext == null) {
        throw new GooglePlayServicesNotAvailableException(i);
      }
      throw new GooglePlayServicesRepairableException(i, "Google Play Services not available", paramContext);
    }
  }
  
  @Deprecated
  public static int zzbo(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo("com.google.android.gms", 0);
      return paramContext.versionCode;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
    }
    return 0;
  }
  
  @Deprecated
  public static void zzbq(Context paramContext)
  {
    if (vd.getAndSet(true)) {}
    for (;;)
    {
      return;
      try
      {
        paramContext = (NotificationManager)paramContext.getSystemService("notification");
        if (paramContext != null)
        {
          paramContext.cancel(10436);
          return;
        }
      }
      catch (SecurityException paramContext) {}
    }
  }
  
  private static void zzbt(Context paramContext)
  {
    if (ve.get()) {}
    do
    {
      return;
      zzbx(paramContext);
      if (vb == 0) {
        throw new IllegalStateException("A required meta-data tag in your app's AndroidManifest.xml does not exist.  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
      }
    } while (vb == GOOGLE_PLAY_SERVICES_VERSION_CODE);
    int i = GOOGLE_PLAY_SERVICES_VERSION_CODE;
    int j = vb;
    paramContext = String.valueOf("com.google.android.gms.version");
    throw new IllegalStateException(String.valueOf(paramContext).length() + 290 + "The meta-data tag in your app's AndroidManifest.xml does not have the right value.  Expected " + i + " but found " + j + ".  You must have the following declaration within the <application> element:     <meta-data android:name=\"" + paramContext + "\" android:value=\"@integer/google_play_services_version\" />");
  }
  
  public static boolean zzbu(Context paramContext)
  {
    zzbx(paramContext);
    return uZ;
  }
  
  public static boolean zzbv(Context paramContext)
  {
    return (zzbu(paramContext)) || (!zzapl());
  }
  
  @TargetApi(18)
  public static boolean zzbw(Context paramContext)
  {
    if (zzs.zzaxq())
    {
      paramContext = ((UserManager)paramContext.getSystemService("user")).getApplicationRestrictions(paramContext.getPackageName());
      if ((paramContext != null) && ("true".equals(paramContext.getString("restricted_profile")))) {
        return true;
      }
    }
    return false;
  }
  
  private static void zzbx(Context paramContext)
  {
    if (!vc) {
      zzby(paramContext);
    }
  }
  
  /* Error */
  private static void zzby(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 177	android/content/Context:getPackageName	()Ljava/lang/String;
    //   4: putstatic 40	com/google/android/gms/common/zze:va	Ljava/lang/String;
    //   7: aload_0
    //   8: invokestatic 415	com/google/android/gms/internal/zzsi:zzcr	(Landroid/content/Context;)Lcom/google/android/gms/internal/zzsh;
    //   11: astore_1
    //   12: aload_0
    //   13: invokestatic 420	com/google/android/gms/common/internal/zzaa:zzch	(Landroid/content/Context;)I
    //   16: putstatic 42	com/google/android/gms/common/zze:vb	I
    //   19: aload_1
    //   20: ldc 8
    //   22: bipush 64
    //   24: invokevirtual 423	com/google/android/gms/internal/zzsh:getPackageInfo	(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
    //   27: astore_1
    //   28: aload_1
    //   29: ifnull +35 -> 64
    //   32: aload_0
    //   33: invokestatic 203	com/google/android/gms/common/zzf:zzbz	(Landroid/content/Context;)Lcom/google/android/gms/common/zzf;
    //   36: aload_1
    //   37: iconst_1
    //   38: anewarray 234	com/google/android/gms/common/zzd$zza
    //   41: dup
    //   42: iconst_0
    //   43: getstatic 209	com/google/android/gms/common/zzd$zzd:uW	[Lcom/google/android/gms/common/zzd$zza;
    //   46: iconst_1
    //   47: aaload
    //   48: aastore
    //   49: invokevirtual 213	com/google/android/gms/common/zzf:zza	(Landroid/content/pm/PackageInfo;[Lcom/google/android/gms/common/zzd$zza;)Lcom/google/android/gms/common/zzd$zza;
    //   52: ifnull +12 -> 64
    //   55: iconst_1
    //   56: putstatic 38	com/google/android/gms/common/zze:uZ	Z
    //   59: iconst_1
    //   60: putstatic 44	com/google/android/gms/common/zze:vc	Z
    //   63: return
    //   64: iconst_0
    //   65: putstatic 38	com/google/android/gms/common/zze:uZ	Z
    //   68: goto -9 -> 59
    //   71: astore_0
    //   72: ldc -41
    //   74: ldc_w 425
    //   77: aload_0
    //   78: invokestatic 427	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   81: pop
    //   82: iconst_1
    //   83: putstatic 44	com/google/android/gms/common/zze:vc	Z
    //   86: return
    //   87: astore_0
    //   88: iconst_1
    //   89: putstatic 44	com/google/android/gms/common/zze:vc	Z
    //   92: aload_0
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	paramContext	Context
    //   11	26	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	28	71	android/content/pm/PackageManager$NameNotFoundException
    //   32	59	71	android/content/pm/PackageManager$NameNotFoundException
    //   64	68	71	android/content/pm/PackageManager$NameNotFoundException
    //   0	28	87	finally
    //   32	59	87	finally
    //   64	68	87	finally
    //   72	82	87	finally
  }
  
  @Deprecated
  public static boolean zzd(Context paramContext, int paramInt)
  {
    if (paramInt == 18) {
      return true;
    }
    if (paramInt == 1) {
      return zzs(paramContext, "com.google.android.gms");
    }
    return false;
  }
  
  @Deprecated
  public static boolean zze(Context paramContext, int paramInt)
  {
    if (paramInt == 9) {
      return zzs(paramContext, "com.android.vending");
    }
    return false;
  }
  
  @Deprecated
  public static boolean zzf(Context paramContext, int paramInt)
  {
    return zzy.zzf(paramContext, paramInt);
  }
  
  @Deprecated
  public static Intent zzfm(int paramInt)
  {
    return zzc.zzapd().zza(null, paramInt, null);
  }
  
  static boolean zzfn(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  @TargetApi(21)
  static boolean zzs(Context paramContext, String paramString)
  {
    boolean bool = paramString.equals("com.google.android.gms");
    if ((bool) && (zzd.zzact())) {
      return false;
    }
    if (zzs.zzaxu())
    {
      localObject = paramContext.getPackageManager().getPackageInstaller().getAllSessions().iterator();
      while (((Iterator)localObject).hasNext()) {
        if (paramString.equals(((PackageInstaller.SessionInfo)((Iterator)localObject).next()).getAppPackageName())) {
          return true;
        }
      }
    }
    Object localObject = paramContext.getPackageManager();
    try
    {
      paramString = ((PackageManager)localObject).getApplicationInfo(paramString, 8192);
      if (bool) {
        return paramString.enabled;
      }
      if (paramString.enabled)
      {
        bool = zzbw(paramContext);
        if (bool) {}
      }
      for (bool = true;; bool = false) {
        return bool;
      }
      return false;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */