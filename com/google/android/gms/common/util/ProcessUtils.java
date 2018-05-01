package com.google.android.gms.common.util;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.annotation.Nullable;

public class ProcessUtils
{
  private static String zzaai = null;
  private static int zzaaj = 0;
  
  @Nullable
  public static String getMyProcessName()
  {
    if (zzaai == null) {
      zzaai = zzl(zzde());
    }
    return zzaai;
  }
  
  private static int zzde()
  {
    if (zzaaj == 0) {
      zzaaj = Process.myPid();
    }
    return zzaaj;
  }
  
  /* Error */
  @Nullable
  private static String zzl(int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: iload_0
    //   3: ifgt +5 -> 8
    //   6: aload_1
    //   7: areturn
    //   8: new 36	java/lang/StringBuilder
    //   11: astore_2
    //   12: aload_2
    //   13: bipush 25
    //   15: invokespecial 40	java/lang/StringBuilder:<init>	(I)V
    //   18: aload_2
    //   19: ldc 42
    //   21: invokevirtual 46	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: iload_0
    //   25: invokevirtual 49	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   28: ldc 51
    //   30: invokevirtual 46	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: invokevirtual 54	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   36: invokestatic 58	com/google/android/gms/common/util/ProcessUtils:zzm	(Ljava/lang/String;)Ljava/io/BufferedReader;
    //   39: astore_2
    //   40: aload_2
    //   41: invokevirtual 63	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   44: invokevirtual 68	java/lang/String:trim	()Ljava/lang/String;
    //   47: astore_3
    //   48: aload_3
    //   49: astore_1
    //   50: aload_2
    //   51: invokestatic 74	com/google/android/gms/common/util/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   54: goto -48 -> 6
    //   57: astore_2
    //   58: aconst_null
    //   59: astore_2
    //   60: aload_2
    //   61: invokestatic 74	com/google/android/gms/common/util/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   64: goto -58 -> 6
    //   67: astore_2
    //   68: aconst_null
    //   69: astore_1
    //   70: aload_1
    //   71: invokestatic 74	com/google/android/gms/common/util/IOUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   74: aload_2
    //   75: athrow
    //   76: astore_1
    //   77: aload_2
    //   78: astore_3
    //   79: aload_1
    //   80: astore_2
    //   81: aload_3
    //   82: astore_1
    //   83: goto -13 -> 70
    //   86: astore_3
    //   87: goto -27 -> 60
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	paramInt	int
    //   1	70	1	localObject1	Object
    //   76	4	1	localObject2	Object
    //   82	1	1	localObject3	Object
    //   11	40	2	localObject4	Object
    //   57	1	2	localIOException1	IOException
    //   59	2	2	localCloseable	java.io.Closeable
    //   67	11	2	localObject5	Object
    //   80	1	2	localObject6	Object
    //   47	35	3	localObject7	Object
    //   86	1	3	localIOException2	IOException
    // Exception table:
    //   from	to	target	type
    //   8	40	57	java/io/IOException
    //   8	40	67	finally
    //   40	48	76	finally
    //   40	48	86	java/io/IOException
  }
  
  private static BufferedReader zzm(String paramString)
    throws IOException
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskReads();
    try
    {
      FileReader localFileReader = new java/io/FileReader;
      localFileReader.<init>(paramString);
      paramString = new BufferedReader(localFileReader);
      return paramString;
    }
    finally
    {
      StrictMode.setThreadPolicy(localThreadPolicy);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/ProcessUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */