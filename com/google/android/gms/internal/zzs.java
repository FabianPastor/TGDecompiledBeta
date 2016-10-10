package com.google.android.gms.internal;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class zzs
{
  public static boolean DEBUG = Log.isLoggable(TAG, 2);
  public static String TAG = "Volley";
  
  public static void zza(String paramString, Object... paramVarArgs)
  {
    if (DEBUG) {
      Log.v(TAG, zzd(paramString, paramVarArgs));
    }
  }
  
  public static void zza(Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    Log.e(TAG, zzd(paramString, paramVarArgs), paramThrowable);
  }
  
  public static void zzb(String paramString, Object... paramVarArgs)
  {
    Log.d(TAG, zzd(paramString, paramVarArgs));
  }
  
  public static void zzc(String paramString, Object... paramVarArgs)
  {
    Log.e(TAG, zzd(paramString, paramVarArgs));
  }
  
  private static String zzd(String paramString, Object... paramVarArgs)
  {
    int i;
    label20:
    String str;
    if (paramVarArgs == null)
    {
      paramVarArgs = new Throwable().fillInStackTrace().getStackTrace();
      i = 2;
      if (i >= paramVarArgs.length) {
        break label176;
      }
      if (paramVarArgs[i].getClass().equals(zzs.class)) {
        break label169;
      }
      str = paramVarArgs[i].getClassName();
      str = str.substring(str.lastIndexOf('.') + 1);
      str = str.substring(str.lastIndexOf('$') + 1);
      paramVarArgs = String.valueOf(paramVarArgs[i].getMethodName());
    }
    label169:
    label176:
    for (paramVarArgs = String.valueOf(str).length() + 1 + String.valueOf(paramVarArgs).length() + str + "." + paramVarArgs;; paramVarArgs = "<unknown>")
    {
      return String.format(Locale.US, "[%d] %s: %s", new Object[] { Long.valueOf(Thread.currentThread().getId()), paramVarArgs, paramString });
      paramString = String.format(Locale.US, paramString, paramVarArgs);
      break;
      i += 1;
      break label20;
    }
  }
  
  static class zza
  {
    public static final boolean zzbj = zzs.DEBUG;
    private final List<zza> zzbk = new ArrayList();
    private boolean zzbl = false;
    
    private long getTotalDuration()
    {
      if (this.zzbk.size() == 0) {
        return 0L;
      }
      long l = ((zza)this.zzbk.get(0)).time;
      return ((zza)this.zzbk.get(this.zzbk.size() - 1)).time - l;
    }
    
    protected void finalize()
      throws Throwable
    {
      if (!this.zzbl)
      {
        zzd("Request on the loose");
        zzs.zzc("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
      }
    }
    
    public void zza(String paramString, long paramLong)
    {
      try
      {
        if (this.zzbl) {
          throw new IllegalStateException("Marker added to finished log");
        }
      }
      finally {}
      this.zzbk.add(new zza(paramString, paramLong, SystemClock.elapsedRealtime()));
    }
    
    /* Error */
    public void zzd(String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: iconst_1
      //   4: putfield 33	com/google/android/gms/internal/zzs$zza:zzbl	Z
      //   7: aload_0
      //   8: invokespecial 86	com/google/android/gms/internal/zzs$zza:getTotalDuration	()J
      //   11: lstore 4
      //   13: lload 4
      //   15: lconst_0
      //   16: lcmp
      //   17: ifgt +6 -> 23
      //   20: aload_0
      //   21: monitorexit
      //   22: return
      //   23: aload_0
      //   24: getfield 31	com/google/android/gms/internal/zzs$zza:zzbk	Ljava/util/List;
      //   27: iconst_0
      //   28: invokeinterface 45 2 0
      //   33: checkcast 9	com/google/android/gms/internal/zzs$zza$zza
      //   36: getfield 49	com/google/android/gms/internal/zzs$zza$zza:time	J
      //   39: lstore_2
      //   40: ldc 88
      //   42: iconst_2
      //   43: anewarray 4	java/lang/Object
      //   46: dup
      //   47: iconst_0
      //   48: lload 4
      //   50: invokestatic 94	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   53: aastore
      //   54: dup
      //   55: iconst_1
      //   56: aload_1
      //   57: aastore
      //   58: invokestatic 97	com/google/android/gms/internal/zzs:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
      //   61: aload_0
      //   62: getfield 31	com/google/android/gms/internal/zzs$zza:zzbk	Ljava/util/List;
      //   65: invokeinterface 101 1 0
      //   70: astore_1
      //   71: aload_1
      //   72: invokeinterface 107 1 0
      //   77: ifeq -57 -> 20
      //   80: aload_1
      //   81: invokeinterface 111 1 0
      //   86: checkcast 9	com/google/android/gms/internal/zzs$zza$zza
      //   89: astore 6
      //   91: aload 6
      //   93: getfield 49	com/google/android/gms/internal/zzs$zza$zza:time	J
      //   96: lstore 4
      //   98: ldc 113
      //   100: iconst_3
      //   101: anewarray 4	java/lang/Object
      //   104: dup
      //   105: iconst_0
      //   106: lload 4
      //   108: lload_2
      //   109: lsub
      //   110: invokestatic 94	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   113: aastore
      //   114: dup
      //   115: iconst_1
      //   116: aload 6
      //   118: getfield 116	com/google/android/gms/internal/zzs$zza$zza:zzbm	J
      //   121: invokestatic 94	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   124: aastore
      //   125: dup
      //   126: iconst_2
      //   127: aload 6
      //   129: getfield 120	com/google/android/gms/internal/zzs$zza$zza:name	Ljava/lang/String;
      //   132: aastore
      //   133: invokestatic 97	com/google/android/gms/internal/zzs:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
      //   136: lload 4
      //   138: lstore_2
      //   139: goto -68 -> 71
      //   142: astore_1
      //   143: aload_0
      //   144: monitorexit
      //   145: aload_1
      //   146: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	147	0	this	zza
      //   0	147	1	paramString	String
      //   39	100	2	l1	long
      //   11	126	4	l2	long
      //   89	39	6	localzza	zza
      // Exception table:
      //   from	to	target	type
      //   2	13	142	finally
      //   23	71	142	finally
      //   71	136	142	finally
    }
    
    private static class zza
    {
      public final String name;
      public final long time;
      public final long zzbm;
      
      public zza(String paramString, long paramLong1, long paramLong2)
      {
        this.name = paramString;
        this.zzbm = paramLong1;
        this.time = paramLong2;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */