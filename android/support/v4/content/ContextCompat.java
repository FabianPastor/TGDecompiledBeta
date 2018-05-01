package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat
{
  private static final Object sLock = new Object();
  private static TypedValue sTempValue;
  
  /* Error */
  private static File createFilesDir(File paramFile)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: astore_1
    //   5: aload_0
    //   6: invokevirtual 24	java/io/File:exists	()Z
    //   9: ifne +23 -> 32
    //   12: aload_0
    //   13: astore_1
    //   14: aload_0
    //   15: invokevirtual 27	java/io/File:mkdirs	()Z
    //   18: ifne +14 -> 32
    //   21: aload_0
    //   22: invokevirtual 24	java/io/File:exists	()Z
    //   25: istore_2
    //   26: iload_2
    //   27: ifeq +10 -> 37
    //   30: aload_0
    //   31: astore_1
    //   32: ldc 2
    //   34: monitorexit
    //   35: aload_1
    //   36: areturn
    //   37: new 29	java/lang/StringBuilder
    //   40: astore_1
    //   41: aload_1
    //   42: invokespecial 30	java/lang/StringBuilder:<init>	()V
    //   45: ldc 32
    //   47: aload_1
    //   48: ldc 34
    //   50: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_0
    //   54: invokevirtual 42	java/io/File:getPath	()Ljava/lang/String;
    //   57: invokevirtual 38	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: invokevirtual 45	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokestatic 51	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   66: pop
    //   67: aconst_null
    //   68: astore_1
    //   69: goto -37 -> 32
    //   72: astore_0
    //   73: ldc 2
    //   75: monitorexit
    //   76: aload_0
    //   77: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	78	0	paramFile	File
    //   4	65	1	localObject	Object
    //   25	2	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   5	12	72	finally
    //   14	26	72	finally
    //   37	67	72	finally
  }
  
  public static int getColor(Context paramContext, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {}
    for (paramInt = paramContext.getColor(paramInt);; paramInt = paramContext.getResources().getColor(paramInt)) {
      return paramInt;
    }
  }
  
  public static Drawable getDrawable(Context paramContext, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      paramContext = paramContext.getDrawable(paramInt);
    }
    for (;;)
    {
      return paramContext;
      if (Build.VERSION.SDK_INT >= 16)
      {
        paramContext = paramContext.getResources().getDrawable(paramInt);
        continue;
      }
      synchronized (sLock)
      {
        if (sTempValue == null)
        {
          TypedValue localTypedValue = new android/util/TypedValue;
          localTypedValue.<init>();
          sTempValue = localTypedValue;
        }
        paramContext.getResources().getValue(paramInt, sTempValue, true);
        paramInt = sTempValue.resourceId;
        paramContext = paramContext.getResources().getDrawable(paramInt);
      }
    }
  }
  
  public static File[] getExternalCacheDirs(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 19) {}
    File[] arrayOfFile;
    for (paramContext = paramContext.getExternalCacheDirs();; paramContext = arrayOfFile)
    {
      return paramContext;
      arrayOfFile = new File[1];
      arrayOfFile[0] = paramContext.getExternalCacheDir();
    }
  }
  
  public static File[] getExternalFilesDirs(Context paramContext, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 19) {}
    File[] arrayOfFile;
    for (paramContext = paramContext.getExternalFilesDirs(paramString);; paramContext = arrayOfFile)
    {
      return paramContext;
      arrayOfFile = new File[1];
      arrayOfFile[0] = paramContext.getExternalFilesDir(paramString);
    }
  }
  
  public static File getNoBackupFilesDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (paramContext = paramContext.getNoBackupFilesDir();; paramContext = createFilesDir(new File(paramContext.getApplicationInfo().dataDir, "no_backup"))) {
      return paramContext;
    }
  }
  
  public static boolean isDeviceProtectedStorage(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 24) {}
    for (boolean bool = paramContext.isDeviceProtectedStorage();; bool = false) {
      return bool;
    }
  }
  
  public static void startActivity(Context paramContext, Intent paramIntent, Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      paramContext.startActivity(paramIntent, paramBundle);
    }
    for (;;)
    {
      return;
      paramContext.startActivity(paramIntent);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/ContextCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */