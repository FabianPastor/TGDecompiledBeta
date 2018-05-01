package com.google.firebase.messaging;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.measurement.zzabi;
import com.google.android.gms.internal.measurement.zzabj;
import com.google.android.gms.internal.measurement.zzabn;
import com.google.android.gms.internal.measurement.zzabo;
import com.google.android.gms.measurement.AppMeasurement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class zzc
{
  /* Error */
  private static Object zza(zzabo paramzzabo, String paramString, zzb paramzzb)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: ldc 10
    //   4: invokestatic 16	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   7: astore 4
    //   9: aload_0
    //   10: getfield 22	com/google/android/gms/internal/measurement/zzabo:zzcag	Ljava/lang/String;
    //   13: aload_0
    //   14: getfield 25	com/google/android/gms/internal/measurement/zzabo:zzcah	Ljava/lang/String;
    //   17: invokestatic 29	com/google/firebase/messaging/zzc:zzw	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/Bundle;
    //   20: astore 5
    //   22: aload 4
    //   24: iconst_0
    //   25: anewarray 12	java/lang/Class
    //   28: invokevirtual 33	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   31: iconst_0
    //   32: anewarray 4	java/lang/Object
    //   35: invokevirtual 39	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   38: astore 6
    //   40: aload 4
    //   42: ldc 41
    //   44: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   47: aload 6
    //   49: aload_1
    //   50: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   53: aload 4
    //   55: ldc 53
    //   57: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   60: aload 6
    //   62: aload_0
    //   63: getfield 57	com/google/android/gms/internal/measurement/zzabo:zzcai	J
    //   66: invokestatic 63	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   69: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   72: aload 4
    //   74: ldc 65
    //   76: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   79: aload 6
    //   81: aload_0
    //   82: getfield 22	com/google/android/gms/internal/measurement/zzabo:zzcag	Ljava/lang/String;
    //   85: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: ldc 67
    //   92: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   95: aload 6
    //   97: aload_0
    //   98: getfield 25	com/google/android/gms/internal/measurement/zzabo:zzcah	Ljava/lang/String;
    //   101: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   104: aload_0
    //   105: getfield 70	com/google/android/gms/internal/measurement/zzabo:zzcaj	Ljava/lang/String;
    //   108: invokestatic 76	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   111: ifeq +191 -> 302
    //   114: aload_3
    //   115: astore_1
    //   116: aload 4
    //   118: ldc 78
    //   120: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   123: aload 6
    //   125: aload_1
    //   126: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   129: aload 4
    //   131: ldc 80
    //   133: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   136: astore_3
    //   137: aload_0
    //   138: getfield 83	com/google/android/gms/internal/measurement/zzabo:zzbsn	Ljava/lang/String;
    //   141: invokestatic 76	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   144: ifne +166 -> 310
    //   147: aload_0
    //   148: getfield 83	com/google/android/gms/internal/measurement/zzabo:zzbsn	Ljava/lang/String;
    //   151: astore_1
    //   152: aload_3
    //   153: aload 6
    //   155: aload_1
    //   156: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   159: aload 4
    //   161: ldc 85
    //   163: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   166: aload 6
    //   168: aload 5
    //   170: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   173: aload 4
    //   175: ldc 87
    //   177: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   180: aload 6
    //   182: aload_0
    //   183: getfield 90	com/google/android/gms/internal/measurement/zzabo:zzcak	J
    //   186: invokestatic 63	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   189: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   192: aload 4
    //   194: ldc 92
    //   196: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   199: astore_3
    //   200: aload_0
    //   201: getfield 95	com/google/android/gms/internal/measurement/zzabo:zzbsm	Ljava/lang/String;
    //   204: invokestatic 76	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   207: ifne +111 -> 318
    //   210: aload_0
    //   211: getfield 95	com/google/android/gms/internal/measurement/zzabo:zzbsm	Ljava/lang/String;
    //   214: astore_1
    //   215: aload_3
    //   216: aload 6
    //   218: aload_1
    //   219: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   222: aload 4
    //   224: ldc 97
    //   226: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   229: aload 6
    //   231: aload 5
    //   233: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   236: aload 4
    //   238: ldc 99
    //   240: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   243: aload 6
    //   245: aload_0
    //   246: getfield 102	com/google/android/gms/internal/measurement/zzabo:zzrp	J
    //   249: invokestatic 63	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   252: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   255: aload 4
    //   257: ldc 104
    //   259: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   262: astore_1
    //   263: aload_0
    //   264: getfield 107	com/google/android/gms/internal/measurement/zzabo:zzbso	Ljava/lang/String;
    //   267: invokestatic 76	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   270: ifne +56 -> 326
    //   273: aload_0
    //   274: getfield 107	com/google/android/gms/internal/measurement/zzabo:zzbso	Ljava/lang/String;
    //   277: astore_0
    //   278: aload_1
    //   279: aload 6
    //   281: aload_0
    //   282: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   285: aload 4
    //   287: ldc 109
    //   289: invokevirtual 45	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   292: aload 6
    //   294: aload 5
    //   296: invokevirtual 51	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   299: aload 6
    //   301: areturn
    //   302: aload_0
    //   303: getfield 70	com/google/android/gms/internal/measurement/zzabo:zzcaj	Ljava/lang/String;
    //   306: astore_1
    //   307: goto -191 -> 116
    //   310: aload_2
    //   311: invokevirtual 115	com/google/firebase/messaging/zzb:zztk	()Ljava/lang/String;
    //   314: astore_1
    //   315: goto -163 -> 152
    //   318: aload_2
    //   319: invokevirtual 118	com/google/firebase/messaging/zzb:zztj	()Ljava/lang/String;
    //   322: astore_1
    //   323: goto -108 -> 215
    //   326: aload_2
    //   327: invokevirtual 121	com/google/firebase/messaging/zzb:zztl	()Ljava/lang/String;
    //   330: astore_0
    //   331: goto -53 -> 278
    //   334: astore_0
    //   335: aconst_null
    //   336: astore 6
    //   338: ldc 123
    //   340: ldc 125
    //   342: aload_0
    //   343: invokestatic 131	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   346: pop
    //   347: goto -48 -> 299
    //   350: astore_0
    //   351: goto -13 -> 338
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	354	0	paramzzabo	zzabo
    //   0	354	1	paramString	String
    //   0	354	2	paramzzb	zzb
    //   1	215	3	localField	Field
    //   7	279	4	localClass	Class
    //   20	275	5	localBundle	Bundle
    //   38	299	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	40	334	java/lang/Exception
    //   40	114	350	java/lang/Exception
    //   116	152	350	java/lang/Exception
    //   152	215	350	java/lang/Exception
    //   215	278	350	java/lang/Exception
    //   278	299	350	java/lang/Exception
    //   302	307	350	java/lang/Exception
    //   310	315	350	java/lang/Exception
    //   318	323	350	java/lang/Exception
    //   326	331	350	java/lang/Exception
  }
  
  private static String zza(zzabo paramzzabo, zzb paramzzb)
  {
    if ((paramzzabo != null) && (!TextUtils.isEmpty(paramzzabo.zzbsp))) {}
    for (paramzzabo = paramzzabo.zzbsp;; paramzzabo = paramzzb.zztm()) {
      return paramzzabo;
    }
  }
  
  private static List<Object> zza(AppMeasurement paramAppMeasurement, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      Method localMethod = AppMeasurement.class.getDeclaredMethod("getConditionalUserProperties", new Class[] { String.class, String.class });
      localMethod.setAccessible(true);
      paramAppMeasurement = (List)localMethod.invoke(paramAppMeasurement, new Object[] { paramString, "" });
      if (Log.isLoggable("FirebaseAbtUtil", 2))
      {
        int i = paramAppMeasurement.size();
        Log.v("FirebaseAbtUtil", String.valueOf(paramString).length() + 55 + "Number of currently set _Es for origin: " + paramString + " is " + i);
      }
      return paramAppMeasurement;
    }
    catch (Exception paramAppMeasurement)
    {
      for (;;)
      {
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramAppMeasurement);
        paramAppMeasurement = localArrayList;
      }
    }
  }
  
  private static void zza(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (Log.isLoggable("FirebaseAbtUtil", 2))
    {
      paramString1 = String.valueOf(paramString1);
      if (paramString1.length() != 0)
      {
        paramString1 = "_CE(experimentId) called by ".concat(paramString1);
        Log.v("FirebaseAbtUtil", paramString1);
      }
    }
    else
    {
      if (zzy(paramContext)) {
        break label56;
      }
    }
    for (;;)
    {
      return;
      paramString1 = new String("_CE(experimentId) called by ");
      break;
      label56:
      AppMeasurement localAppMeasurement = zzx(paramContext);
      try
      {
        paramContext = AppMeasurement.class.getDeclaredMethod("clearConditionalUserProperty", new Class[] { String.class, String.class, Bundle.class });
        paramContext.setAccessible(true);
        if (Log.isLoggable("FirebaseAbtUtil", 2))
        {
          int i = String.valueOf(paramString2).length();
          int j = String.valueOf(paramString3).length();
          paramString1 = new java/lang/StringBuilder;
          paramString1.<init>(i + 17 + j);
          Log.v("FirebaseAbtUtil", "Clearing _E: [" + paramString2 + ", " + paramString3 + "]");
        }
        paramContext.invoke(localAppMeasurement, new Object[] { paramString2, paramString4, zzw(paramString2, paramString3) });
      }
      catch (Exception paramContext)
      {
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramContext);
      }
    }
  }
  
  public static void zza(Context paramContext, String paramString, byte[] paramArrayOfByte, zzb paramzzb, int paramInt)
  {
    Object localObject1;
    if (Log.isLoggable("FirebaseAbtUtil", 2))
    {
      localObject1 = String.valueOf(paramString);
      if (((String)localObject1).length() != 0)
      {
        localObject1 = "_SE called by ".concat((String)localObject1);
        Log.v("FirebaseAbtUtil", (String)localObject1);
      }
    }
    else
    {
      if (zzy(paramContext)) {
        break label62;
      }
    }
    for (;;)
    {
      return;
      localObject1 = new String("_SE called by ");
      break;
      label62:
      localObject1 = zzx(paramContext);
      paramArrayOfByte = zzi(paramArrayOfByte);
      if (paramArrayOfByte == null)
      {
        if (Log.isLoggable("FirebaseAbtUtil", 2)) {
          Log.v("FirebaseAbtUtil", "_SE failed; either _P was not set, or we couldn't deserialize the _P.");
        }
      }
      else
      {
        Object localObject2;
        Object localObject3;
        Object localObject4;
        String str;
        int i;
        for (;;)
        {
          int m;
          try
          {
            Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
            localObject2 = zza((AppMeasurement)localObject1, paramString).iterator();
            paramInt = 0;
            if (!((Iterator)localObject2).hasNext()) {
              break label642;
            }
            localObject3 = ((Iterator)localObject2).next();
            localObject4 = zzs(localObject3);
            str = zzt(localObject3);
            long l = ((Long)Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mCreationTimestamp").get(localObject3)).longValue();
            if ((paramArrayOfByte.zzcag.equals(localObject4)) && (paramArrayOfByte.zzcah.equals(str)))
            {
              if (Log.isLoggable("FirebaseAbtUtil", 2))
              {
                i = String.valueOf(localObject4).length();
                paramInt = String.valueOf(str).length();
                localObject3 = new java/lang/StringBuilder;
                ((StringBuilder)localObject3).<init>(i + 23 + paramInt);
                Log.v("FirebaseAbtUtil", "_E is already set. [" + (String)localObject4 + ", " + str + "]");
              }
              paramInt = 1;
              continue;
            }
            int j = 0;
            localObject3 = paramArrayOfByte.zzcam;
            int k = localObject3.length;
            i = 0;
            m = j;
            if (i < k)
            {
              if (localObject3[i].zzcag.equals(localObject4))
              {
                if (Log.isLoggable("FirebaseAbtUtil", 2))
                {
                  m = String.valueOf(localObject4).length();
                  i = String.valueOf(str).length();
                  localObject3 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject3).<init>(m + 33 + i);
                  Log.v("FirebaseAbtUtil", "_E is found in the _OE list. [" + (String)localObject4 + ", " + str + "]");
                }
                m = 1;
              }
            }
            else
            {
              if (m != 0) {
                continue;
              }
              if (paramArrayOfByte.zzcai <= l) {
                break label555;
              }
              if (Log.isLoggable("FirebaseAbtUtil", 2))
              {
                m = String.valueOf(localObject4).length();
                i = String.valueOf(str).length();
                localObject3 = new java/lang/StringBuilder;
                ((StringBuilder)localObject3).<init>(m + 115 + i);
                Log.v("FirebaseAbtUtil", "Clearing _E as it was not in the _OE list, andits start time is older than the start time of the _E to be set. [" + (String)localObject4 + ", " + str + "]");
              }
              zza(paramContext, paramString, (String)localObject4, str, zza(paramArrayOfByte, paramzzb));
              continue;
            }
          }
          catch (Exception paramContext)
          {
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramContext);
          }
          i++;
          continue;
          label555:
          if (Log.isLoggable("FirebaseAbtUtil", 2))
          {
            i = String.valueOf(localObject4).length();
            m = String.valueOf(str).length();
            localObject3 = new java/lang/StringBuilder;
            ((StringBuilder)localObject3).<init>(i + 109 + m);
            Log.v("FirebaseAbtUtil", "_E was not found in the _OE list, but not clearing it as it has a new start time than the _E to be set.  [" + (String)localObject4 + ", " + str + "]");
          }
        }
        label642:
        if (paramInt != 0)
        {
          if (Log.isLoggable("FirebaseAbtUtil", 2))
          {
            paramContext = paramArrayOfByte.zzcag;
            paramString = paramArrayOfByte.zzcah;
            i = String.valueOf(paramContext).length();
            paramInt = String.valueOf(paramString).length();
            paramArrayOfByte = new java/lang/StringBuilder;
            paramArrayOfByte.<init>(i + 44 + paramInt);
            Log.v("FirebaseAbtUtil", "_E is already set. Not setting it again [" + paramContext + ", " + paramString + "]");
          }
        }
        else
        {
          if (Log.isLoggable("FirebaseAbtUtil", 2))
          {
            str = paramArrayOfByte.zzcag;
            localObject2 = paramArrayOfByte.zzcah;
            i = String.valueOf(str).length();
            paramInt = String.valueOf(localObject2).length();
            localObject4 = new java/lang/StringBuilder;
            ((StringBuilder)localObject4).<init>(i + 7 + paramInt);
            Log.v("FirebaseAbtUtil", "_SEI: " + str + " " + (String)localObject2);
          }
          for (;;)
          {
            try
            {
              Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
              localObject2 = zza((AppMeasurement)localObject1, paramString);
              paramInt = zzb((AppMeasurement)localObject1, paramString);
              if (zza((AppMeasurement)localObject1, paramString).size() >= paramInt)
              {
                if (paramArrayOfByte.zzcal != 0)
                {
                  paramInt = paramArrayOfByte.zzcal;
                  if (paramInt != 1) {
                    break label1172;
                  }
                  localObject4 = ((List)localObject2).get(0);
                  str = zzs(localObject4);
                  localObject3 = zzt(localObject4);
                  if (Log.isLoggable("FirebaseAbtUtil", 2))
                  {
                    paramInt = String.valueOf(str).length();
                    localObject4 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject4).<init>(paramInt + 38);
                    Log.v("FirebaseAbtUtil", "Clearing _E due to overflow policy: [" + str + "]");
                  }
                  zza(paramContext, paramString, str, (String)localObject3, zza(paramArrayOfByte, paramzzb));
                }
              }
              else
              {
                localObject2 = ((List)localObject2).iterator();
                if (!((Iterator)localObject2).hasNext()) {
                  break label1262;
                }
                localObject4 = ((Iterator)localObject2).next();
                str = zzs(localObject4);
                localObject3 = zzt(localObject4);
                if ((!str.equals(paramArrayOfByte.zzcag)) || (((String)localObject3).equals(paramArrayOfByte.zzcah)) || (!Log.isLoggable("FirebaseAbtUtil", 2))) {
                  continue;
                }
                i = String.valueOf(str).length();
                paramInt = String.valueOf(localObject3).length();
                localObject4 = new java/lang/StringBuilder;
                ((StringBuilder)localObject4).<init>(i + 77 + paramInt);
                Log.v("FirebaseAbtUtil", "Clearing _E, as only one _V of the same _E can be set atany given time: [" + str + ", " + (String)localObject3 + "].");
                zza(paramContext, paramString, str, (String)localObject3, zza(paramArrayOfByte, paramzzb));
                continue;
              }
            }
            catch (Exception paramContext)
            {
              Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramContext);
            }
            paramInt = 1;
          }
          label1172:
          if (Log.isLoggable("FirebaseAbtUtil", 2))
          {
            paramContext = paramArrayOfByte.zzcag;
            paramString = paramArrayOfByte.zzcah;
            paramInt = String.valueOf(paramContext).length();
            i = String.valueOf(paramString).length();
            paramArrayOfByte = new java/lang/StringBuilder;
            paramArrayOfByte.<init>(paramInt + 44 + i);
            Log.v("FirebaseAbtUtil", "_E won't be set due to overflow policy. [" + paramContext + ", " + paramString + "]");
            continue;
            label1262:
            paramContext = zza(paramArrayOfByte, paramString, paramzzb);
            if (paramContext == null)
            {
              if (Log.isLoggable("FirebaseAbtUtil", 2))
              {
                paramContext = paramArrayOfByte.zzcag;
                paramString = paramArrayOfByte.zzcah;
                paramInt = String.valueOf(paramContext).length();
                i = String.valueOf(paramString).length();
                paramArrayOfByte = new java/lang/StringBuilder;
                paramArrayOfByte.<init>(paramInt + 42 + i);
                Log.v("FirebaseAbtUtil", "Could not create _CUP for: [" + paramContext + ", " + paramString + "]. Skipping.");
              }
            }
            else {
              try
              {
                paramString = AppMeasurement.class.getDeclaredMethod("setConditionalUserProperty", new Class[] { Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty") });
                paramString.setAccessible(true);
                paramString.invoke(localObject1, new Object[] { paramContext });
              }
              catch (Exception paramContext)
              {
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramContext);
              }
            }
          }
        }
      }
    }
  }
  
  private static int zzb(AppMeasurement paramAppMeasurement, String paramString)
  {
    try
    {
      Method localMethod = AppMeasurement.class.getDeclaredMethod("getMaxUserProperties", new Class[] { String.class });
      localMethod.setAccessible(true);
      i = ((Integer)localMethod.invoke(paramAppMeasurement, new Object[] { paramString })).intValue();
      return i;
    }
    catch (Exception paramAppMeasurement)
    {
      for (;;)
      {
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramAppMeasurement);
        int i = 20;
      }
    }
  }
  
  private static zzabo zzi(byte[] paramArrayOfByte)
  {
    try
    {
      zzabo localzzabo = new com/google/android/gms/internal/measurement/zzabo;
      localzzabo.<init>();
      paramArrayOfByte = (zzabo)zzabj.zza(localzzabo, paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (zzabi paramArrayOfByte)
    {
      for (;;)
      {
        paramArrayOfByte = null;
      }
    }
  }
  
  private static String zzs(Object paramObject)
    throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
  {
    return (String)Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mName").get(paramObject);
  }
  
  private static String zzt(Object paramObject)
    throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
  {
    return (String)Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mValue").get(paramObject);
  }
  
  private static Bundle zzw(String paramString1, String paramString2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString(paramString1, paramString2);
    return localBundle;
  }
  
  private static AppMeasurement zzx(Context paramContext)
  {
    try
    {
      paramContext = AppMeasurement.getInstance(paramContext);
      return paramContext;
    }
    catch (NoClassDefFoundError paramContext)
    {
      for (;;)
      {
        paramContext = null;
      }
    }
  }
  
  private static boolean zzy(Context paramContext)
  {
    boolean bool1 = false;
    boolean bool2;
    if (zzx(paramContext) == null)
    {
      bool2 = bool1;
      if (Log.isLoggable("FirebaseAbtUtil", 2))
      {
        Log.v("FirebaseAbtUtil", "Firebase Analytics not available");
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      try
      {
        Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
        bool2 = true;
      }
      catch (ClassNotFoundException paramContext)
      {
        bool2 = bool1;
      }
      if (Log.isLoggable("FirebaseAbtUtil", 2))
      {
        Log.v("FirebaseAbtUtil", "Firebase Analytics library is missing support for abt. Please update to a more recent version.");
        bool2 = bool1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/messaging/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */