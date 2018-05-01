package org.telegram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.lang.reflect.Field;

public class NativeLoader
{
  private static final String LIB_NAME = "tmessages.28";
  private static final String LIB_SO_NAME = "libtmessages.28.so";
  private static final int LIB_VERSION = 28;
  private static final String LOCALE_LIB_SO_NAME = "libtmessages.28loc.so";
  private static volatile boolean nativeLoaded = false;
  private String crashPath = "";
  
  private static File getNativeLibraryDir(Context paramContext)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramContext != null) {}
    try
    {
      localObject2 = new java/io/File;
      ((File)localObject2).<init>((String)ApplicationInfo.class.getField("nativeLibraryDir").get(paramContext.getApplicationInfo()));
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new File(paramContext.getApplicationInfo().dataDir, "lib");
      }
      if (((File)localObject1).isDirectory()) {
        return (File)localObject1;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        localThrowable.printStackTrace();
        Object localObject3 = localObject1;
        continue;
        localObject1 = null;
      }
    }
  }
  
  private static native void init(String paramString, boolean paramBoolean);
  
  /* Error */
  @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode"})
  public static void initNativeLibs(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 24	org/telegram/messenger/NativeLoader:nativeLoaded	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +7 -> 15
    //   11: ldc 2
    //   13: monitorexit
    //   14: return
    //   15: aload_0
    //   16: invokestatic 96	net/hockeyapp/android/Constants:loadFromContext	(Landroid/content/Context;)V
    //   19: ldc 8
    //   21: invokestatic 101	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   24: iconst_1
    //   25: putstatic 24	org/telegram/messenger/NativeLoader:nativeLoaded	Z
    //   28: getstatic 106	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   31: ifeq -20 -> 11
    //   34: ldc 108
    //   36: invokestatic 113	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   39: goto -28 -> 11
    //   42: astore_2
    //   43: aload_2
    //   44: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   47: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   50: astore_2
    //   51: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   54: ldc 124
    //   56: invokevirtual 128	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   59: ifeq +184 -> 243
    //   62: ldc 124
    //   64: astore_2
    //   65: ldc -126
    //   67: invokestatic 134	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   70: astore_3
    //   71: aload_2
    //   72: astore 4
    //   74: aload_3
    //   75: ifnull +19 -> 94
    //   78: aload_2
    //   79: astore 4
    //   81: aload_3
    //   82: ldc -120
    //   84: invokevirtual 140	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   87: ifeq +7 -> 94
    //   90: ldc -114
    //   92: astore 4
    //   94: new 38	java/io/File
    //   97: astore_2
    //   98: aload_2
    //   99: aload_0
    //   100: invokevirtual 146	android/content/Context:getFilesDir	()Ljava/io/File;
    //   103: ldc 70
    //   105: invokespecial 149	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   108: aload_2
    //   109: invokevirtual 152	java/io/File:mkdirs	()Z
    //   112: pop
    //   113: new 38	java/io/File
    //   116: astore_3
    //   117: aload_3
    //   118: aload_2
    //   119: ldc 17
    //   121: invokespecial 149	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   124: aload_3
    //   125: invokevirtual 155	java/io/File:exists	()Z
    //   128: istore_1
    //   129: iload_1
    //   130: ifeq +40 -> 170
    //   133: getstatic 106	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   136: ifeq +8 -> 144
    //   139: ldc -99
    //   141: invokestatic 113	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   144: aload_3
    //   145: invokevirtual 161	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   148: invokestatic 164	java/lang/System:load	(Ljava/lang/String;)V
    //   151: iconst_1
    //   152: putstatic 24	org/telegram/messenger/NativeLoader:nativeLoaded	Z
    //   155: goto -144 -> 11
    //   158: astore 5
    //   160: aload 5
    //   162: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   165: aload_3
    //   166: invokevirtual 167	java/io/File:delete	()Z
    //   169: pop
    //   170: getstatic 106	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   173: ifeq +31 -> 204
    //   176: new 169	java/lang/StringBuilder
    //   179: astore 5
    //   181: aload 5
    //   183: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   186: aload 5
    //   188: ldc -84
    //   190: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: aload 4
    //   195: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   201: invokestatic 181	org/telegram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   204: aload_0
    //   205: aload_2
    //   206: aload_3
    //   207: aload 4
    //   209: invokestatic 185	org/telegram/messenger/NativeLoader:loadFromZip	(Landroid/content/Context;Ljava/io/File;Ljava/io/File;Ljava/lang/String;)Z
    //   212: istore_1
    //   213: iload_1
    //   214: ifne -203 -> 11
    //   217: ldc 8
    //   219: invokestatic 101	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   222: iconst_1
    //   223: putstatic 24	org/telegram/messenger/NativeLoader:nativeLoaded	Z
    //   226: goto -215 -> 11
    //   229: astore_0
    //   230: aload_0
    //   231: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   234: goto -223 -> 11
    //   237: astore_0
    //   238: ldc 2
    //   240: monitorexit
    //   241: aload_0
    //   242: athrow
    //   243: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   246: ldc -69
    //   248: invokevirtual 128	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   251: ifeq +9 -> 260
    //   254: ldc -69
    //   256: astore_2
    //   257: goto -192 -> 65
    //   260: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   263: ldc -67
    //   265: invokevirtual 128	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   268: ifeq +9 -> 277
    //   271: ldc -67
    //   273: astore_2
    //   274: goto -209 -> 65
    //   277: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   280: ldc -65
    //   282: invokevirtual 128	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   285: ifeq +9 -> 294
    //   288: ldc -65
    //   290: astore_2
    //   291: goto -226 -> 65
    //   294: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   297: ldc -114
    //   299: invokevirtual 128	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   302: ifeq +9 -> 311
    //   305: ldc -114
    //   307: astore_2
    //   308: goto -243 -> 65
    //   311: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   314: ldc -63
    //   316: invokevirtual 128	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   319: ifeq +9 -> 328
    //   322: ldc -63
    //   324: astore_2
    //   325: goto -260 -> 65
    //   328: ldc -65
    //   330: astore 4
    //   332: aload 4
    //   334: astore_2
    //   335: getstatic 106	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   338: ifeq -273 -> 65
    //   341: new 169	java/lang/StringBuilder
    //   344: astore_2
    //   345: aload_2
    //   346: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   349: aload_2
    //   350: ldc -61
    //   352: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   355: getstatic 122	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   358: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   361: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   364: invokestatic 181	org/telegram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   367: aload 4
    //   369: astore_2
    //   370: goto -305 -> 65
    //   373: astore_2
    //   374: aload_2
    //   375: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   378: ldc -65
    //   380: astore_2
    //   381: goto -316 -> 65
    //   384: astore_0
    //   385: aload_0
    //   386: invokevirtual 80	java/lang/Throwable:printStackTrace	()V
    //   389: goto -172 -> 217
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	392	0	paramContext	Context
    //   6	208	1	bool	boolean
    //   42	2	2	localError1	Error
    //   50	320	2	localObject1	Object
    //   373	2	2	localException	Exception
    //   380	1	2	str	String
    //   70	137	3	localObject2	Object
    //   72	296	4	localObject3	Object
    //   158	3	5	localError2	Error
    //   179	8	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   19	39	42	java/lang/Error
    //   133	144	158	java/lang/Error
    //   144	155	158	java/lang/Error
    //   217	226	229	java/lang/Error
    //   3	7	237	finally
    //   15	19	237	finally
    //   19	39	237	finally
    //   43	47	237	finally
    //   47	62	237	finally
    //   65	71	237	finally
    //   81	90	237	finally
    //   94	129	237	finally
    //   133	144	237	finally
    //   144	155	237	finally
    //   160	170	237	finally
    //   170	204	237	finally
    //   204	213	237	finally
    //   217	226	237	finally
    //   230	234	237	finally
    //   243	254	237	finally
    //   260	271	237	finally
    //   277	288	237	finally
    //   294	305	237	finally
    //   311	322	237	finally
    //   335	367	237	finally
    //   374	378	237	finally
    //   385	389	237	finally
    //   47	62	373	java/lang/Exception
    //   243	254	373	java/lang/Exception
    //   260	271	373	java/lang/Exception
    //   277	288	373	java/lang/Exception
    //   294	305	373	java/lang/Exception
    //   311	322	373	java/lang/Exception
    //   335	367	373	java/lang/Exception
    //   19	39	384	java/lang/Throwable
    //   43	47	384	java/lang/Throwable
    //   47	62	384	java/lang/Throwable
    //   65	71	384	java/lang/Throwable
    //   81	90	384	java/lang/Throwable
    //   94	129	384	java/lang/Throwable
    //   133	144	384	java/lang/Throwable
    //   144	155	384	java/lang/Throwable
    //   160	170	384	java/lang/Throwable
    //   170	204	384	java/lang/Throwable
    //   204	213	384	java/lang/Throwable
    //   243	254	384	java/lang/Throwable
    //   260	271	384	java/lang/Throwable
    //   277	288	384	java/lang/Throwable
    //   294	305	384	java/lang/Throwable
    //   311	322	384	java/lang/Throwable
    //   335	367	384	java/lang/Throwable
    //   374	378	384	java/lang/Throwable
  }
  
  /* Error */
  @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode", "SetWorldReadable"})
  private static boolean loadFromZip(Context paramContext, File paramFile1, File paramFile2, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 201	java/io/File:listFiles	()[Ljava/io/File;
    //   4: astore_1
    //   5: aload_1
    //   6: arraylength
    //   7: istore 4
    //   9: iconst_0
    //   10: istore 5
    //   12: iload 5
    //   14: iload 4
    //   16: if_icmpge +22 -> 38
    //   19: aload_1
    //   20: iload 5
    //   22: aaload
    //   23: invokevirtual 167	java/io/File:delete	()Z
    //   26: pop
    //   27: iinc 5 1
    //   30: goto -18 -> 12
    //   33: astore_1
    //   34: aload_1
    //   35: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   38: aconst_null
    //   39: astore 6
    //   41: aconst_null
    //   42: astore 7
    //   44: aconst_null
    //   45: astore 8
    //   47: aconst_null
    //   48: astore 9
    //   50: aconst_null
    //   51: astore 10
    //   53: aconst_null
    //   54: astore 11
    //   56: aload 10
    //   58: astore 12
    //   60: aload 6
    //   62: astore_1
    //   63: new 203	java/util/zip/ZipFile
    //   66: astore 13
    //   68: aload 10
    //   70: astore 12
    //   72: aload 6
    //   74: astore_1
    //   75: aload 13
    //   77: aload_0
    //   78: invokevirtual 54	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   81: getfield 206	android/content/pm/ApplicationInfo:sourceDir	Ljava/lang/String;
    //   84: invokespecial 207	java/util/zip/ZipFile:<init>	(Ljava/lang/String;)V
    //   87: aload 11
    //   89: astore_1
    //   90: aload 9
    //   92: astore_0
    //   93: new 169	java/lang/StringBuilder
    //   96: astore 12
    //   98: aload 11
    //   100: astore_1
    //   101: aload 9
    //   103: astore_0
    //   104: aload 12
    //   106: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   109: aload 11
    //   111: astore_1
    //   112: aload 9
    //   114: astore_0
    //   115: aload 13
    //   117: aload 12
    //   119: ldc -47
    //   121: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_3
    //   125: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: ldc -45
    //   130: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: ldc 11
    //   135: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   141: invokevirtual 215	java/util/zip/ZipFile:getEntry	(Ljava/lang/String;)Ljava/util/zip/ZipEntry;
    //   144: astore 12
    //   146: aload 12
    //   148: ifnonnull +114 -> 262
    //   151: aload 11
    //   153: astore_1
    //   154: aload 9
    //   156: astore_0
    //   157: new 91	java/lang/Exception
    //   160: astore_2
    //   161: aload 11
    //   163: astore_1
    //   164: aload 9
    //   166: astore_0
    //   167: new 169	java/lang/StringBuilder
    //   170: astore 12
    //   172: aload 11
    //   174: astore_1
    //   175: aload 9
    //   177: astore_0
    //   178: aload 12
    //   180: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   183: aload 11
    //   185: astore_1
    //   186: aload 9
    //   188: astore_0
    //   189: aload_2
    //   190: aload 12
    //   192: ldc -39
    //   194: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: aload_3
    //   198: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: ldc -45
    //   203: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: ldc 8
    //   208: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   214: invokespecial 218	java/lang/Exception:<init>	(Ljava/lang/String;)V
    //   217: aload 11
    //   219: astore_1
    //   220: aload 9
    //   222: astore_0
    //   223: aload_2
    //   224: athrow
    //   225: astore_3
    //   226: aload 13
    //   228: astore_0
    //   229: aload_1
    //   230: astore_2
    //   231: aload_2
    //   232: astore 12
    //   234: aload_0
    //   235: astore_1
    //   236: aload_3
    //   237: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   240: aload_2
    //   241: ifnull +7 -> 248
    //   244: aload_2
    //   245: invokevirtual 223	java/io/InputStream:close	()V
    //   248: aload_0
    //   249: ifnull +7 -> 256
    //   252: aload_0
    //   253: invokevirtual 224	java/util/zip/ZipFile:close	()V
    //   256: iconst_0
    //   257: istore 14
    //   259: iload 14
    //   261: ireturn
    //   262: aload 11
    //   264: astore_1
    //   265: aload 9
    //   267: astore_0
    //   268: aload 13
    //   270: aload 12
    //   272: invokevirtual 228	java/util/zip/ZipFile:getInputStream	(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;
    //   275: astore_3
    //   276: aload_3
    //   277: astore_1
    //   278: aload_3
    //   279: astore_0
    //   280: new 230	java/io/FileOutputStream
    //   283: astore 11
    //   285: aload_3
    //   286: astore_1
    //   287: aload_3
    //   288: astore_0
    //   289: aload 11
    //   291: aload_2
    //   292: invokespecial 233	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   295: aload_3
    //   296: astore_1
    //   297: aload_3
    //   298: astore_0
    //   299: sipush 4096
    //   302: newarray <illegal type>
    //   304: astore 12
    //   306: aload_3
    //   307: astore_1
    //   308: aload_3
    //   309: astore_0
    //   310: aload_3
    //   311: aload 12
    //   313: invokevirtual 237	java/io/InputStream:read	([B)I
    //   316: istore 5
    //   318: iload 5
    //   320: ifle +49 -> 369
    //   323: aload_3
    //   324: astore_1
    //   325: aload_3
    //   326: astore_0
    //   327: invokestatic 242	java/lang/Thread:yield	()V
    //   330: aload_3
    //   331: astore_1
    //   332: aload_3
    //   333: astore_0
    //   334: aload 11
    //   336: aload 12
    //   338: iconst_0
    //   339: iload 5
    //   341: invokevirtual 248	java/io/OutputStream:write	([BII)V
    //   344: goto -38 -> 306
    //   347: astore_1
    //   348: aload 13
    //   350: astore_2
    //   351: aload_0
    //   352: ifnull +7 -> 359
    //   355: aload_0
    //   356: invokevirtual 223	java/io/InputStream:close	()V
    //   359: aload_2
    //   360: ifnull +7 -> 367
    //   363: aload_2
    //   364: invokevirtual 224	java/util/zip/ZipFile:close	()V
    //   367: aload_1
    //   368: athrow
    //   369: aload_3
    //   370: astore_1
    //   371: aload_3
    //   372: astore_0
    //   373: aload 11
    //   375: invokevirtual 249	java/io/OutputStream:close	()V
    //   378: aload_3
    //   379: astore_1
    //   380: aload_3
    //   381: astore_0
    //   382: aload_2
    //   383: iconst_1
    //   384: iconst_0
    //   385: invokevirtual 253	java/io/File:setReadable	(ZZ)Z
    //   388: pop
    //   389: aload_3
    //   390: astore_1
    //   391: aload_3
    //   392: astore_0
    //   393: aload_2
    //   394: iconst_1
    //   395: iconst_0
    //   396: invokevirtual 256	java/io/File:setExecutable	(ZZ)Z
    //   399: pop
    //   400: aload_3
    //   401: astore_1
    //   402: aload_3
    //   403: astore_0
    //   404: aload_2
    //   405: iconst_1
    //   406: invokevirtual 260	java/io/File:setWritable	(Z)Z
    //   409: pop
    //   410: aload_3
    //   411: astore_1
    //   412: aload_3
    //   413: astore_0
    //   414: aload_2
    //   415: invokevirtual 161	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   418: invokestatic 164	java/lang/System:load	(Ljava/lang/String;)V
    //   421: aload_3
    //   422: astore_1
    //   423: aload_3
    //   424: astore_0
    //   425: iconst_1
    //   426: putstatic 24	org/telegram/messenger/NativeLoader:nativeLoaded	Z
    //   429: iconst_1
    //   430: istore 14
    //   432: aload_3
    //   433: ifnull +7 -> 440
    //   436: aload_3
    //   437: invokevirtual 223	java/io/InputStream:close	()V
    //   440: aload 13
    //   442: ifnull +8 -> 450
    //   445: aload 13
    //   447: invokevirtual 224	java/util/zip/ZipFile:close	()V
    //   450: goto -191 -> 259
    //   453: astore_2
    //   454: aload_3
    //   455: astore_1
    //   456: aload_3
    //   457: astore_0
    //   458: aload_2
    //   459: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   462: goto -33 -> 429
    //   465: astore_0
    //   466: aload_0
    //   467: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   470: goto -30 -> 440
    //   473: astore_0
    //   474: aload_0
    //   475: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   478: goto -28 -> 450
    //   481: astore_1
    //   482: aload_1
    //   483: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   486: goto -238 -> 248
    //   489: astore_0
    //   490: aload_0
    //   491: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   494: goto -238 -> 256
    //   497: astore_0
    //   498: aload_0
    //   499: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   502: goto -143 -> 359
    //   505: astore_0
    //   506: aload_0
    //   507: invokestatic 117	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   510: goto -143 -> 367
    //   513: astore_3
    //   514: aload 12
    //   516: astore_0
    //   517: aload_1
    //   518: astore_2
    //   519: aload_3
    //   520: astore_1
    //   521: goto -170 -> 351
    //   524: astore_3
    //   525: aload 8
    //   527: astore_2
    //   528: aload 7
    //   530: astore_0
    //   531: goto -300 -> 231
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	534	0	paramContext	Context
    //   0	534	1	paramFile1	File
    //   0	534	2	paramFile2	File
    //   0	534	3	paramString	String
    //   7	10	4	i	int
    //   10	330	5	j	int
    //   39	34	6	localObject1	Object
    //   42	487	7	localObject2	Object
    //   45	481	8	localObject3	Object
    //   48	218	9	localObject4	Object
    //   51	18	10	localObject5	Object
    //   54	320	11	localFileOutputStream	java.io.FileOutputStream
    //   58	457	12	localObject6	Object
    //   66	380	13	localZipFile	java.util.zip.ZipFile
    //   257	174	14	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   0	9	33	java/lang/Exception
    //   19	27	33	java/lang/Exception
    //   93	98	225	java/lang/Exception
    //   104	109	225	java/lang/Exception
    //   115	146	225	java/lang/Exception
    //   157	161	225	java/lang/Exception
    //   167	172	225	java/lang/Exception
    //   178	183	225	java/lang/Exception
    //   189	217	225	java/lang/Exception
    //   223	225	225	java/lang/Exception
    //   268	276	225	java/lang/Exception
    //   280	285	225	java/lang/Exception
    //   289	295	225	java/lang/Exception
    //   299	306	225	java/lang/Exception
    //   310	318	225	java/lang/Exception
    //   327	330	225	java/lang/Exception
    //   334	344	225	java/lang/Exception
    //   373	378	225	java/lang/Exception
    //   382	389	225	java/lang/Exception
    //   393	400	225	java/lang/Exception
    //   404	410	225	java/lang/Exception
    //   414	421	225	java/lang/Exception
    //   425	429	225	java/lang/Exception
    //   458	462	225	java/lang/Exception
    //   93	98	347	finally
    //   104	109	347	finally
    //   115	146	347	finally
    //   157	161	347	finally
    //   167	172	347	finally
    //   178	183	347	finally
    //   189	217	347	finally
    //   223	225	347	finally
    //   268	276	347	finally
    //   280	285	347	finally
    //   289	295	347	finally
    //   299	306	347	finally
    //   310	318	347	finally
    //   327	330	347	finally
    //   334	344	347	finally
    //   373	378	347	finally
    //   382	389	347	finally
    //   393	400	347	finally
    //   404	410	347	finally
    //   414	421	347	finally
    //   425	429	347	finally
    //   458	462	347	finally
    //   414	421	453	java/lang/Error
    //   425	429	453	java/lang/Error
    //   436	440	465	java/lang/Exception
    //   445	450	473	java/lang/Exception
    //   244	248	481	java/lang/Exception
    //   252	256	489	java/lang/Exception
    //   355	359	497	java/lang/Exception
    //   363	367	505	java/lang/Exception
    //   63	68	513	finally
    //   75	87	513	finally
    //   236	240	513	finally
    //   63	68	524	java/lang/Exception
    //   75	87	524	java/lang/Exception
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NativeLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */