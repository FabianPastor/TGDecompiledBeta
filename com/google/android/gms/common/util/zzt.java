package com.google.android.gms.common.util;

import android.os.Binder;
import android.os.Process;

public class zzt
{
  private static String EY = null;
  
  public static String zzaxx()
  {
    return zzhl(Binder.getCallingPid());
  }
  
  public static String zzaxy()
  {
    if (EY == null) {
      EY = zzhl(Process.myPid());
    }
    return EY;
  }
  
  /* Error */
  private static String zzhl(int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: new 35	java/io/BufferedReader
    //   6: dup
    //   7: new 37	java/io/FileReader
    //   10: dup
    //   11: new 39	java/lang/StringBuilder
    //   14: dup
    //   15: bipush 25
    //   17: invokespecial 43	java/lang/StringBuilder:<init>	(I)V
    //   20: ldc 45
    //   22: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: iload_0
    //   26: invokevirtual 52	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: ldc 54
    //   31: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   37: invokespecial 60	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   40: invokespecial 63	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   43: astore_2
    //   44: aload_2
    //   45: astore_1
    //   46: aload_2
    //   47: invokevirtual 66	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   50: invokevirtual 71	java/lang/String:trim	()Ljava/lang/String;
    //   53: astore_3
    //   54: aload_3
    //   55: astore_1
    //   56: aload_1
    //   57: astore_3
    //   58: aload_2
    //   59: ifnull +9 -> 68
    //   62: aload_2
    //   63: invokevirtual 74	java/io/BufferedReader:close	()V
    //   66: aload_1
    //   67: astore_3
    //   68: aload_3
    //   69: areturn
    //   70: astore_2
    //   71: ldc 76
    //   73: aload_2
    //   74: invokevirtual 79	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   77: aload_2
    //   78: invokestatic 85	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   81: pop
    //   82: aload_1
    //   83: areturn
    //   84: astore_3
    //   85: aconst_null
    //   86: astore_2
    //   87: aload_2
    //   88: astore_1
    //   89: ldc 76
    //   91: aload_3
    //   92: invokevirtual 86	java/io/IOException:getMessage	()Ljava/lang/String;
    //   95: aload_3
    //   96: invokestatic 89	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   99: pop
    //   100: aload 4
    //   102: astore_3
    //   103: aload_2
    //   104: ifnull -36 -> 68
    //   107: aload_2
    //   108: invokevirtual 74	java/io/BufferedReader:close	()V
    //   111: aconst_null
    //   112: areturn
    //   113: astore_1
    //   114: ldc 76
    //   116: aload_1
    //   117: invokevirtual 79	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   120: aload_1
    //   121: invokestatic 85	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   124: pop
    //   125: aconst_null
    //   126: areturn
    //   127: astore_2
    //   128: aconst_null
    //   129: astore_1
    //   130: aload_1
    //   131: ifnull +7 -> 138
    //   134: aload_1
    //   135: invokevirtual 74	java/io/BufferedReader:close	()V
    //   138: aload_2
    //   139: athrow
    //   140: astore_1
    //   141: ldc 76
    //   143: aload_1
    //   144: invokevirtual 79	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   147: aload_1
    //   148: invokestatic 85	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   151: pop
    //   152: goto -14 -> 138
    //   155: astore_2
    //   156: goto -26 -> 130
    //   159: astore_3
    //   160: goto -73 -> 87
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	paramInt	int
    //   45	44	1	localObject1	Object
    //   113	8	1	localException1	Exception
    //   129	6	1	localObject2	Object
    //   140	8	1	localException2	Exception
    //   43	20	2	localBufferedReader	java.io.BufferedReader
    //   70	8	2	localException3	Exception
    //   86	22	2	localObject3	Object
    //   127	12	2	localObject4	Object
    //   155	1	2	localObject5	Object
    //   53	16	3	localObject6	Object
    //   84	12	3	localIOException1	java.io.IOException
    //   102	1	3	localObject7	Object
    //   159	1	3	localIOException2	java.io.IOException
    //   1	100	4	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   62	66	70	java/lang/Exception
    //   3	44	84	java/io/IOException
    //   107	111	113	java/lang/Exception
    //   3	44	127	finally
    //   134	138	140	java/lang/Exception
    //   46	54	155	finally
    //   89	100	155	finally
    //   46	54	159	java/io/IOException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */