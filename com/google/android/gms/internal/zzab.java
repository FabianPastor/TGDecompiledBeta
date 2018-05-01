package com.google.android.gms.internal;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class zzab
{
  public static boolean DEBUG = Log.isLoggable("Volley", 2);
  private static String TAG = "Volley";
  
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
      if (paramVarArgs[i].getClass().equals(zzab.class)) {
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
  
  static final class zza
  {
    public static final boolean zzai = zzab.DEBUG;
    private final List<zzac> zzaj = new ArrayList();
    private boolean zzak = false;
    
    protected final void finalize()
      throws Throwable
    {
      if (!this.zzak)
      {
        zzc("Request on the loose");
        zzab.zzc("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
      }
    }
    
    public final void zza(String paramString, long paramLong)
    {
      try
      {
        if (this.zzak) {
          throw new IllegalStateException("Marker added to finished log");
        }
      }
      finally {}
      this.zzaj.add(new zzac(paramString, paramLong, SystemClock.elapsedRealtime()));
    }
    
    /* Error */
    public final void zzc(String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: iconst_1
      //   4: putfield 31	com/google/android/gms/internal/zzab$zza:zzak	Z
      //   7: aload_0
      //   8: getfield 29	com/google/android/gms/internal/zzab$zza:zzaj	Ljava/util/List;
      //   11: invokeinterface 74 1 0
      //   16: istore_2
      //   17: iload_2
      //   18: ifne +14 -> 32
      //   21: lconst_0
      //   22: lstore_3
      //   23: lload_3
      //   24: lconst_0
      //   25: lcmp
      //   26: ifgt +55 -> 81
      //   29: aload_0
      //   30: monitorexit
      //   31: return
      //   32: aload_0
      //   33: getfield 29	com/google/android/gms/internal/zzab$zza:zzaj	Ljava/util/List;
      //   36: iconst_0
      //   37: invokeinterface 78 2 0
      //   42: checkcast 55	com/google/android/gms/internal/zzac
      //   45: getfield 82	com/google/android/gms/internal/zzac:time	J
      //   48: lstore_3
      //   49: aload_0
      //   50: getfield 29	com/google/android/gms/internal/zzab$zza:zzaj	Ljava/util/List;
      //   53: aload_0
      //   54: getfield 29	com/google/android/gms/internal/zzab$zza:zzaj	Ljava/util/List;
      //   57: invokeinterface 74 1 0
      //   62: iconst_1
      //   63: isub
      //   64: invokeinterface 78 2 0
      //   69: checkcast 55	com/google/android/gms/internal/zzac
      //   72: getfield 82	com/google/android/gms/internal/zzac:time	J
      //   75: lload_3
      //   76: lsub
      //   77: lstore_3
      //   78: goto -55 -> 23
      //   81: aload_0
      //   82: getfield 29	com/google/android/gms/internal/zzab$zza:zzaj	Ljava/util/List;
      //   85: iconst_0
      //   86: invokeinterface 78 2 0
      //   91: checkcast 55	com/google/android/gms/internal/zzac
      //   94: getfield 82	com/google/android/gms/internal/zzac:time	J
      //   97: lstore 5
      //   99: ldc 84
      //   101: iconst_2
      //   102: anewarray 4	java/lang/Object
      //   105: dup
      //   106: iconst_0
      //   107: lload_3
      //   108: invokestatic 90	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   111: aastore
      //   112: dup
      //   113: iconst_1
      //   114: aload_1
      //   115: aastore
      //   116: invokestatic 93	com/google/android/gms/internal/zzab:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
      //   119: aload_0
      //   120: getfield 29	com/google/android/gms/internal/zzab$zza:zzaj	Ljava/util/List;
      //   123: invokeinterface 97 1 0
      //   128: astore_1
      //   129: lload 5
      //   131: lstore_3
      //   132: aload_1
      //   133: invokeinterface 103 1 0
      //   138: ifeq -109 -> 29
      //   141: aload_1
      //   142: invokeinterface 107 1 0
      //   147: checkcast 55	com/google/android/gms/internal/zzac
      //   150: astore 7
      //   152: aload 7
      //   154: getfield 82	com/google/android/gms/internal/zzac:time	J
      //   157: lstore 5
      //   159: ldc 109
      //   161: iconst_3
      //   162: anewarray 4	java/lang/Object
      //   165: dup
      //   166: iconst_0
      //   167: lload 5
      //   169: lload_3
      //   170: lsub
      //   171: invokestatic 90	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   174: aastore
      //   175: dup
      //   176: iconst_1
      //   177: aload 7
      //   179: getfield 112	com/google/android/gms/internal/zzac:zzal	J
      //   182: invokestatic 90	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   185: aastore
      //   186: dup
      //   187: iconst_2
      //   188: aload 7
      //   190: getfield 116	com/google/android/gms/internal/zzac:name	Ljava/lang/String;
      //   193: aastore
      //   194: invokestatic 93	com/google/android/gms/internal/zzab:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
      //   197: lload 5
      //   199: lstore_3
      //   200: goto -68 -> 132
      //   203: astore_1
      //   204: aload_0
      //   205: monitorexit
      //   206: aload_1
      //   207: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	208	0	this	zza
      //   0	208	1	paramString	String
      //   16	2	2	i	int
      //   22	178	3	l1	long
      //   97	101	5	l2	long
      //   150	39	7	localzzac	zzac
      // Exception table:
      //   from	to	target	type
      //   2	17	203	finally
      //   32	78	203	finally
      //   81	129	203	finally
      //   132	197	203	finally
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */