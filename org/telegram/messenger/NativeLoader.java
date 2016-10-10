package org.telegram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import java.io.File;
import java.lang.reflect.Field;
import net.hockeyapp.android.Constants;

public class NativeLoader
{
  private static final String LIB_NAME = "tmessages.24";
  private static final String LIB_SO_NAME = "libtmessages.24.so";
  private static final int LIB_VERSION = 24;
  private static final String LOCALE_LIB_SO_NAME = "libtmessages.24loc.so";
  private static volatile boolean nativeLoaded = false;
  private String crashPath = "";
  
  private static File getNativeLibraryDir(Context paramContext)
  {
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if (paramContext != null) {}
    try
    {
      localObject1 = new File((String)ApplicationInfo.class.getField("nativeLibraryDir").get(paramContext.getApplicationInfo()));
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = new File(paramContext.getApplicationInfo().dataDir, "lib");
      }
      if (((File)localObject3).isDirectory()) {
        return (File)localObject3;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        localThrowable.printStackTrace();
        Object localObject2 = localObject3;
      }
    }
    return null;
  }
  
  private static native void init(String paramString, boolean paramBoolean);
  
  public static void initNativeLibs(Context paramContext)
  {
    for (;;)
    {
      boolean bool;
      try
      {
        bool = nativeLoaded;
        if (bool) {
          return;
        }
        Constants.loadFromContext(paramContext);
      }
      finally {}
      try
      {
        if (!Build.CPU_ABI.equalsIgnoreCase("armeabi-v7a")) {
          break label286;
        }
        localObject1 = "armeabi-v7a";
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        String str = "armeabi";
        continue;
      }
      catch (Throwable paramContext)
      {
        paramContext.printStackTrace();
        continue;
        continue;
      }
      Object localObject4 = System.getProperty("os.arch");
      Object localObject3 = localObject1;
      if (localObject4 != null)
      {
        localObject3 = localObject1;
        if (((String)localObject4).contains("686")) {
          localObject3 = "x86";
        }
      }
      Object localObject1 = getNativeLibraryDir(paramContext);
      if (localObject1 != null)
      {
        if (!new File((File)localObject1, "libtmessages.24.so").exists()) {
          break;
        }
        FileLog.d("tmessages", "load normal lib");
        try
        {
          System.loadLibrary("tmessages.24");
          init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
          nativeLoaded = true;
        }
        catch (Error localError1)
        {
          FileLog.e("tmessages", localError1);
          break;
        }
      }
      else
      {
        Object localObject2 = new File(paramContext.getFilesDir(), "lib");
        ((File)localObject2).mkdirs();
        localObject4 = new File((File)localObject2, "libtmessages.24loc.so");
        bool = ((File)localObject4).exists();
        if (bool)
        {
          try
          {
            FileLog.d("tmessages", "Load local lib");
            System.load(((File)localObject4).getAbsolutePath());
            init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
            nativeLoaded = true;
          }
          catch (Error localError2)
          {
            FileLog.e("tmessages", localError2);
            ((File)localObject4).delete();
          }
        }
        else
        {
          FileLog.e("tmessages", "Library not found, arch = " + (String)localObject3);
          bool = loadFromZip(paramContext, (File)localObject2, (File)localObject4, (String)localObject3);
          if (!bool)
          {
            try
            {
              System.loadLibrary("tmessages.24");
              init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
              nativeLoaded = true;
            }
            catch (Error paramContext)
            {
              FileLog.e("tmessages", paramContext);
            }
            continue;
            label286:
            if (Build.CPU_ABI.equalsIgnoreCase("armeabi"))
            {
              localObject2 = "armeabi";
            }
            else if (Build.CPU_ABI.equalsIgnoreCase("x86"))
            {
              localObject2 = "x86";
            }
            else if (Build.CPU_ABI.equalsIgnoreCase("mips"))
            {
              localObject2 = "mips";
            }
            else
            {
              localObject2 = "armeabi";
              FileLog.e("tmessages", "Unsupported arch: " + Build.CPU_ABI);
            }
          }
        }
      }
    }
  }
  
  /* Error */
  private static boolean loadFromZip(Context paramContext, File paramFile1, File paramFile2, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 201	java/io/File:listFiles	()[Ljava/io/File;
    //   4: astore_1
    //   5: aload_1
    //   6: arraylength
    //   7: istore 5
    //   9: iconst_0
    //   10: istore 4
    //   12: iload 4
    //   14: iload 5
    //   16: if_icmpge +27 -> 43
    //   19: aload_1
    //   20: iload 4
    //   22: aaload
    //   23: invokevirtual 173	java/io/File:delete	()Z
    //   26: pop
    //   27: iload 4
    //   29: iconst_1
    //   30: iadd
    //   31: istore 4
    //   33: goto -21 -> 12
    //   36: astore_1
    //   37: ldc -126
    //   39: aload_1
    //   40: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   43: aconst_null
    //   44: astore 6
    //   46: aconst_null
    //   47: astore 8
    //   49: aconst_null
    //   50: astore 11
    //   52: aconst_null
    //   53: astore 10
    //   55: aconst_null
    //   56: astore_1
    //   57: aconst_null
    //   58: astore 9
    //   60: new 203	java/util/zip/ZipFile
    //   63: dup
    //   64: aload_0
    //   65: invokevirtual 54	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   68: getfield 206	android/content/pm/ApplicationInfo:sourceDir	Ljava/lang/String;
    //   71: invokespecial 207	java/util/zip/ZipFile:<init>	(Ljava/lang/String;)V
    //   74: astore 7
    //   76: aload 9
    //   78: astore_1
    //   79: aload 10
    //   81: astore_0
    //   82: aload 7
    //   84: new 175	java/lang/StringBuilder
    //   87: dup
    //   88: invokespecial 176	java/lang/StringBuilder:<init>	()V
    //   91: ldc -47
    //   93: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: aload_3
    //   97: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: ldc -45
    //   102: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: ldc 11
    //   107: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: invokevirtual 185	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokevirtual 215	java/util/zip/ZipFile:getEntry	(Ljava/lang/String;)Ljava/util/zip/ZipEntry;
    //   116: astore 6
    //   118: aload 6
    //   120: ifnonnull +81 -> 201
    //   123: aload 9
    //   125: astore_1
    //   126: aload 10
    //   128: astore_0
    //   129: new 86	java/lang/Exception
    //   132: dup
    //   133: new 175	java/lang/StringBuilder
    //   136: dup
    //   137: invokespecial 176	java/lang/StringBuilder:<init>	()V
    //   140: ldc -39
    //   142: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: aload_3
    //   146: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   149: ldc -45
    //   151: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: ldc 8
    //   156: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: invokevirtual 185	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   162: invokespecial 218	java/lang/Exception:<init>	(Ljava/lang/String;)V
    //   165: athrow
    //   166: astore_3
    //   167: aload 7
    //   169: astore_0
    //   170: aload_1
    //   171: astore_2
    //   172: aload_2
    //   173: astore_1
    //   174: aload_0
    //   175: astore 6
    //   177: ldc -126
    //   179: aload_3
    //   180: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   183: aload_2
    //   184: ifnull +7 -> 191
    //   187: aload_2
    //   188: invokevirtual 223	java/io/InputStream:close	()V
    //   191: aload_0
    //   192: ifnull +7 -> 199
    //   195: aload_0
    //   196: invokevirtual 224	java/util/zip/ZipFile:close	()V
    //   199: iconst_0
    //   200: ireturn
    //   201: aload 9
    //   203: astore_1
    //   204: aload 10
    //   206: astore_0
    //   207: aload 7
    //   209: aload 6
    //   211: invokevirtual 228	java/util/zip/ZipFile:getInputStream	(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;
    //   214: astore_3
    //   215: aload_3
    //   216: astore_1
    //   217: aload_3
    //   218: astore_0
    //   219: new 230	java/io/FileOutputStream
    //   222: dup
    //   223: aload_2
    //   224: invokespecial 233	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   227: astore 6
    //   229: aload_3
    //   230: astore_1
    //   231: aload_3
    //   232: astore_0
    //   233: sipush 4096
    //   236: newarray <illegal type>
    //   238: astore 8
    //   240: aload_3
    //   241: astore_1
    //   242: aload_3
    //   243: astore_0
    //   244: aload_3
    //   245: aload 8
    //   247: invokevirtual 237	java/io/InputStream:read	([B)I
    //   250: istore 4
    //   252: iload 4
    //   254: ifle +52 -> 306
    //   257: aload_3
    //   258: astore_1
    //   259: aload_3
    //   260: astore_0
    //   261: invokestatic 242	java/lang/Thread:yield	()V
    //   264: aload_3
    //   265: astore_1
    //   266: aload_3
    //   267: astore_0
    //   268: aload 6
    //   270: aload 8
    //   272: iconst_0
    //   273: iload 4
    //   275: invokevirtual 248	java/io/OutputStream:write	([BII)V
    //   278: goto -38 -> 240
    //   281: astore_1
    //   282: aload 7
    //   284: astore 6
    //   286: aload_0
    //   287: ifnull +7 -> 294
    //   290: aload_0
    //   291: invokevirtual 223	java/io/InputStream:close	()V
    //   294: aload 6
    //   296: ifnull +8 -> 304
    //   299: aload 6
    //   301: invokevirtual 224	java/util/zip/ZipFile:close	()V
    //   304: aload_1
    //   305: athrow
    //   306: aload_3
    //   307: astore_1
    //   308: aload_3
    //   309: astore_0
    //   310: aload 6
    //   312: invokevirtual 249	java/io/OutputStream:close	()V
    //   315: aload_3
    //   316: astore_1
    //   317: aload_3
    //   318: astore_0
    //   319: aload_2
    //   320: iconst_1
    //   321: iconst_0
    //   322: invokevirtual 253	java/io/File:setReadable	(ZZ)Z
    //   325: pop
    //   326: aload_3
    //   327: astore_1
    //   328: aload_3
    //   329: astore_0
    //   330: aload_2
    //   331: iconst_1
    //   332: iconst_0
    //   333: invokevirtual 256	java/io/File:setExecutable	(ZZ)Z
    //   336: pop
    //   337: aload_3
    //   338: astore_1
    //   339: aload_3
    //   340: astore_0
    //   341: aload_2
    //   342: iconst_1
    //   343: invokevirtual 260	java/io/File:setWritable	(Z)Z
    //   346: pop
    //   347: aload_3
    //   348: astore_1
    //   349: aload_3
    //   350: astore_0
    //   351: aload_2
    //   352: invokevirtual 167	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   355: invokestatic 170	java/lang/System:load	(Ljava/lang/String;)V
    //   358: aload_3
    //   359: astore_1
    //   360: aload_3
    //   361: astore_0
    //   362: getstatic 143	net/hockeyapp/android/Constants:FILES_PATH	Ljava/lang/String;
    //   365: getstatic 148	org/telegram/messenger/BuildVars:DEBUG_VERSION	Z
    //   368: invokestatic 150	org/telegram/messenger/NativeLoader:init	(Ljava/lang/String;Z)V
    //   371: aload_3
    //   372: astore_1
    //   373: aload_3
    //   374: astore_0
    //   375: iconst_1
    //   376: putstatic 24	org/telegram/messenger/NativeLoader:nativeLoaded	Z
    //   379: aload_3
    //   380: ifnull +7 -> 387
    //   383: aload_3
    //   384: invokevirtual 223	java/io/InputStream:close	()V
    //   387: aload 7
    //   389: ifnull +8 -> 397
    //   392: aload 7
    //   394: invokevirtual 224	java/util/zip/ZipFile:close	()V
    //   397: iconst_1
    //   398: ireturn
    //   399: astore_2
    //   400: aload_3
    //   401: astore_1
    //   402: aload_3
    //   403: astore_0
    //   404: ldc -126
    //   406: aload_2
    //   407: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   410: goto -31 -> 379
    //   413: astore_0
    //   414: ldc -126
    //   416: aload_0
    //   417: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   420: goto -33 -> 387
    //   423: astore_0
    //   424: ldc -126
    //   426: aload_0
    //   427: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   430: goto -33 -> 397
    //   433: astore_1
    //   434: ldc -126
    //   436: aload_1
    //   437: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   440: goto -249 -> 191
    //   443: astore_0
    //   444: ldc -126
    //   446: aload_0
    //   447: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   450: goto -251 -> 199
    //   453: astore_0
    //   454: ldc -126
    //   456: aload_0
    //   457: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   460: goto -166 -> 294
    //   463: astore_0
    //   464: ldc -126
    //   466: aload_0
    //   467: invokestatic 154	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   470: goto -166 -> 304
    //   473: astore_2
    //   474: aload_1
    //   475: astore_0
    //   476: aload_2
    //   477: astore_1
    //   478: goto -192 -> 286
    //   481: astore_3
    //   482: aload 11
    //   484: astore_2
    //   485: aload 8
    //   487: astore_0
    //   488: goto -316 -> 172
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	491	0	paramContext	Context
    //   0	491	1	paramFile1	File
    //   0	491	2	paramFile2	File
    //   0	491	3	paramString	String
    //   10	264	4	i	int
    //   7	10	5	j	int
    //   44	267	6	localObject1	Object
    //   74	319	7	localZipFile	java.util.zip.ZipFile
    //   47	439	8	arrayOfByte	byte[]
    //   58	144	9	localObject2	Object
    //   53	152	10	localObject3	Object
    //   50	433	11	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   0	9	36	java/lang/Exception
    //   19	27	36	java/lang/Exception
    //   82	118	166	java/lang/Exception
    //   129	166	166	java/lang/Exception
    //   207	215	166	java/lang/Exception
    //   219	229	166	java/lang/Exception
    //   233	240	166	java/lang/Exception
    //   244	252	166	java/lang/Exception
    //   261	264	166	java/lang/Exception
    //   268	278	166	java/lang/Exception
    //   310	315	166	java/lang/Exception
    //   319	326	166	java/lang/Exception
    //   330	337	166	java/lang/Exception
    //   341	347	166	java/lang/Exception
    //   351	358	166	java/lang/Exception
    //   362	371	166	java/lang/Exception
    //   375	379	166	java/lang/Exception
    //   404	410	166	java/lang/Exception
    //   82	118	281	finally
    //   129	166	281	finally
    //   207	215	281	finally
    //   219	229	281	finally
    //   233	240	281	finally
    //   244	252	281	finally
    //   261	264	281	finally
    //   268	278	281	finally
    //   310	315	281	finally
    //   319	326	281	finally
    //   330	337	281	finally
    //   341	347	281	finally
    //   351	358	281	finally
    //   362	371	281	finally
    //   375	379	281	finally
    //   404	410	281	finally
    //   351	358	399	java/lang/Error
    //   362	371	399	java/lang/Error
    //   375	379	399	java/lang/Error
    //   383	387	413	java/lang/Exception
    //   392	397	423	java/lang/Exception
    //   187	191	433	java/lang/Exception
    //   195	199	443	java/lang/Exception
    //   290	294	453	java/lang/Exception
    //   299	304	463	java/lang/Exception
    //   60	76	473	finally
    //   177	183	473	finally
    //   60	76	481	java/lang/Exception
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NativeLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */