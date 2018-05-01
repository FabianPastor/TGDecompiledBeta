package com.google.android.gms.iid;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class zzl
{
  private static String zzifp = null;
  private static boolean zzifq = false;
  private static int zzifr = 0;
  private static int zzifs = 0;
  private static int zzift = 0;
  private static BroadcastReceiver zzifu = null;
  private Context zzair;
  private PendingIntent zzicn;
  private Messenger zzicr;
  private Map<String, Object> zzifv = new HashMap();
  private Messenger zzifw;
  private MessengerCompat zzifx;
  private long zzify;
  private long zzifz;
  private int zziga;
  private int zzigb;
  private long zzigc;
  
  public zzl(Context paramContext)
  {
    this.zzair = paramContext;
  }
  
  /* Error */
  private static String zza(KeyPair paramKeyPair, String... paramVarArgs)
  {
    // Byte code:
    //   0: ldc 66
    //   2: aload_1
    //   3: invokestatic 72	android/text/TextUtils:join	(Ljava/lang/CharSequence;[Ljava/lang/Object;)Ljava/lang/String;
    //   6: ldc 74
    //   8: invokevirtual 80	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   11: astore_1
    //   12: aload_0
    //   13: invokevirtual 86	java/security/KeyPair:getPrivate	()Ljava/security/PrivateKey;
    //   16: astore_2
    //   17: aload_2
    //   18: instanceof 88
    //   21: ifeq +43 -> 64
    //   24: ldc 90
    //   26: astore_0
    //   27: aload_0
    //   28: invokestatic 96	java/security/Signature:getInstance	(Ljava/lang/String;)Ljava/security/Signature;
    //   31: astore_0
    //   32: aload_0
    //   33: aload_2
    //   34: invokevirtual 100	java/security/Signature:initSign	(Ljava/security/PrivateKey;)V
    //   37: aload_0
    //   38: aload_1
    //   39: invokevirtual 104	java/security/Signature:update	([B)V
    //   42: aload_0
    //   43: invokevirtual 108	java/security/Signature:sign	()[B
    //   46: invokestatic 114	com/google/android/gms/iid/InstanceID:zzo	([B)Ljava/lang/String;
    //   49: astore_0
    //   50: aload_0
    //   51: areturn
    //   52: astore_0
    //   53: ldc 116
    //   55: ldc 118
    //   57: aload_0
    //   58: invokestatic 124	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   61: pop
    //   62: aconst_null
    //   63: areturn
    //   64: ldc 126
    //   66: astore_0
    //   67: goto -40 -> 27
    //   70: astore_0
    //   71: ldc 116
    //   73: ldc -128
    //   75: aload_0
    //   76: invokestatic 124	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   79: pop
    //   80: aconst_null
    //   81: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	82	0	paramKeyPair	KeyPair
    //   0	82	1	paramVarArgs	String[]
    //   16	18	2	localPrivateKey	java.security.PrivateKey
    // Exception table:
    //   from	to	target	type
    //   0	12	52	java/io/UnsupportedEncodingException
    //   12	24	70	java/security/GeneralSecurityException
    //   27	50	70	java/security/GeneralSecurityException
  }
  
  private static boolean zza(PackageManager paramPackageManager)
  {
    Iterator localIterator = paramPackageManager.queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0).iterator();
    while (localIterator.hasNext()) {
      if (zza(paramPackageManager, ((ResolveInfo)localIterator.next()).activityInfo.packageName, "com.google.iid.TOKEN_REQUEST"))
      {
        zzifq = true;
        return true;
      }
    }
    return false;
  }
  
  private static boolean zza(PackageManager paramPackageManager, String paramString1, String paramString2)
  {
    if (paramPackageManager.checkPermission("com.google.android.c2dm.permission.SEND", paramString1) == 0) {
      return zzb(paramPackageManager, paramString1);
    }
    Log.w("InstanceID/Rpc", String.valueOf(paramString1).length() + 56 + String.valueOf(paramString2).length() + "Possible malicious package " + paramString1 + " declares " + paramString2 + " without permission");
    return false;
  }
  
  private final void zzae(Object paramObject)
  {
    synchronized (getClass())
    {
      Iterator localIterator = this.zzifv.keySet().iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = this.zzifv.get(str);
        this.zzifv.put(str, paramObject);
        zze(localObject, paramObject);
      }
    }
  }
  
  private final void zzavh()
  {
    if (this.zzicr != null) {
      return;
    }
    zzdp(this.zzair);
    this.zzicr = new Messenger(new zzm(this, Looper.getMainLooper()));
  }
  
  private static String zzavi()
  {
    try
    {
      int i = zzift;
      zzift = i + 1;
      String str = Integer.toString(i);
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  private final Intent zzb(Bundle arg1, KeyPair paramKeyPair)
    throws IOException
  {
    // Byte code:
    //   0: new 275	android/os/ConditionVariable
    //   3: dup
    //   4: invokespecial 276	android/os/ConditionVariable:<init>	()V
    //   7: astore 11
    //   9: invokestatic 278	com/google/android/gms/iid/zzl:zzavi	()Ljava/lang/String;
    //   12: astore 10
    //   14: aload_0
    //   15: invokevirtual 218	java/lang/Object:getClass	()Ljava/lang/Class;
    //   18: astore 9
    //   20: aload 9
    //   22: monitorenter
    //   23: aload_0
    //   24: getfield 56	com/google/android/gms/iid/zzl:zzifv	Ljava/util/Map;
    //   27: aload 10
    //   29: aload 11
    //   31: invokeinterface 235 3 0
    //   36: pop
    //   37: aload 9
    //   39: monitorexit
    //   40: invokestatic 284	android/os/SystemClock:elapsedRealtime	()J
    //   43: lstore 4
    //   45: aload_0
    //   46: getfield 286	com/google/android/gms/iid/zzl:zzigc	J
    //   49: lconst_0
    //   50: lcmp
    //   51: ifeq +83 -> 134
    //   54: lload 4
    //   56: aload_0
    //   57: getfield 286	com/google/android/gms/iid/zzl:zzigc	J
    //   60: lcmp
    //   61: ifgt +73 -> 134
    //   64: aload_0
    //   65: getfield 286	com/google/android/gms/iid/zzl:zzigc	J
    //   68: lstore 6
    //   70: aload_0
    //   71: getfield 288	com/google/android/gms/iid/zzl:zzigb	I
    //   74: istore_3
    //   75: ldc 116
    //   77: new 184	java/lang/StringBuilder
    //   80: dup
    //   81: bipush 78
    //   83: invokespecial 195	java/lang/StringBuilder:<init>	(I)V
    //   86: ldc_w 290
    //   89: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: lload 6
    //   94: lload 4
    //   96: lsub
    //   97: invokevirtual 293	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   100: ldc_w 295
    //   103: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: iload_3
    //   107: invokevirtual 298	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   110: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 212	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: new 271	java/io/IOException
    //   120: dup
    //   121: ldc_w 300
    //   124: invokespecial 301	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   127: athrow
    //   128: astore_1
    //   129: aload 9
    //   131: monitorexit
    //   132: aload_1
    //   133: athrow
    //   134: aload_0
    //   135: invokespecial 303	com/google/android/gms/iid/zzl:zzavh	()V
    //   138: getstatic 36	com/google/android/gms/iid/zzl:zzifp	Ljava/lang/String;
    //   141: ifnonnull +14 -> 155
    //   144: new 271	java/io/IOException
    //   147: dup
    //   148: ldc_w 305
    //   151: invokespecial 301	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   154: athrow
    //   155: aload_0
    //   156: invokestatic 284	android/os/SystemClock:elapsedRealtime	()J
    //   159: putfield 307	com/google/android/gms/iid/zzl:zzify	J
    //   162: getstatic 38	com/google/android/gms/iid/zzl:zzifq	Z
    //   165: ifeq +443 -> 608
    //   168: ldc -123
    //   170: astore 9
    //   172: new 131	android/content/Intent
    //   175: dup
    //   176: aload 9
    //   178: invokespecial 136	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   181: astore 9
    //   183: aload 9
    //   185: getstatic 36	com/google/android/gms/iid/zzl:zzifp	Ljava/lang/String;
    //   188: invokevirtual 311	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
    //   191: pop
    //   192: aload_1
    //   193: ldc_w 313
    //   196: aload_0
    //   197: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   200: invokestatic 317	com/google/android/gms/iid/zzl:zzdq	(Landroid/content/Context;)I
    //   203: invokestatic 268	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   206: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   209: aload_1
    //   210: ldc_w 325
    //   213: getstatic 330	android/os/Build$VERSION:SDK_INT	I
    //   216: invokestatic 268	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   219: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   222: aload_1
    //   223: ldc_w 332
    //   226: aload_0
    //   227: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   230: invokestatic 335	com/google/android/gms/iid/InstanceID:zzdm	(Landroid/content/Context;)I
    //   233: invokestatic 268	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   236: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   239: aload_1
    //   240: ldc_w 337
    //   243: aload_0
    //   244: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   247: invokestatic 340	com/google/android/gms/iid/InstanceID:zzdn	(Landroid/content/Context;)Ljava/lang/String;
    //   250: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   253: aload_1
    //   254: ldc_w 342
    //   257: ldc_w 344
    //   260: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   263: aload_1
    //   264: ldc_w 346
    //   267: aload_2
    //   268: invokestatic 349	com/google/android/gms/iid/InstanceID:zza	(Ljava/security/KeyPair;)Ljava/lang/String;
    //   271: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   274: aload_2
    //   275: invokevirtual 353	java/security/KeyPair:getPublic	()Ljava/security/PublicKey;
    //   278: invokeinterface 358 1 0
    //   283: invokestatic 114	com/google/android/gms/iid/InstanceID:zzo	([B)Ljava/lang/String;
    //   286: astore 12
    //   288: aload_1
    //   289: ldc_w 360
    //   292: aload 12
    //   294: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   297: aload_1
    //   298: ldc_w 362
    //   301: aload_2
    //   302: iconst_2
    //   303: anewarray 76	java/lang/String
    //   306: dup
    //   307: iconst_0
    //   308: aload_0
    //   309: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   312: invokevirtual 367	android/content/Context:getPackageName	()Ljava/lang/String;
    //   315: aastore
    //   316: dup
    //   317: iconst_1
    //   318: aload 12
    //   320: aastore
    //   321: invokestatic 369	com/google/android/gms/iid/zzl:zza	(Ljava/security/KeyPair;[Ljava/lang/String;)Ljava/lang/String;
    //   324: invokevirtual 323	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   327: aload 9
    //   329: aload_1
    //   330: invokevirtual 373	android/content/Intent:putExtras	(Landroid/os/Bundle;)Landroid/content/Intent;
    //   333: pop
    //   334: aload_0
    //   335: aload 9
    //   337: invokespecial 377	com/google/android/gms/iid/zzl:zzi	(Landroid/content/Intent;)V
    //   340: aload_0
    //   341: invokestatic 284	android/os/SystemClock:elapsedRealtime	()J
    //   344: putfield 307	com/google/android/gms/iid/zzl:zzify	J
    //   347: aload 9
    //   349: ldc_w 379
    //   352: new 184	java/lang/StringBuilder
    //   355: dup
    //   356: aload 10
    //   358: invokestatic 188	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   361: invokevirtual 192	java/lang/String:length	()I
    //   364: iconst_5
    //   365: iadd
    //   366: invokespecial 195	java/lang/StringBuilder:<init>	(I)V
    //   369: ldc_w 381
    //   372: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   375: aload 10
    //   377: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   380: ldc_w 383
    //   383: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   389: invokevirtual 387	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   392: pop
    //   393: aload 9
    //   395: ldc_w 389
    //   398: new 184	java/lang/StringBuilder
    //   401: dup
    //   402: aload 10
    //   404: invokestatic 188	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   407: invokevirtual 192	java/lang/String:length	()I
    //   410: iconst_5
    //   411: iadd
    //   412: invokespecial 195	java/lang/StringBuilder:<init>	(I)V
    //   415: ldc_w 381
    //   418: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   421: aload 10
    //   423: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   426: ldc_w 383
    //   429: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   432: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   435: invokevirtual 387	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   438: pop
    //   439: ldc_w 391
    //   442: getstatic 36	com/google/android/gms/iid/zzl:zzifp	Ljava/lang/String;
    //   445: invokevirtual 395	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   448: istore 8
    //   450: aload 9
    //   452: ldc_w 397
    //   455: invokevirtual 401	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   458: astore_1
    //   459: aload_1
    //   460: ifnull +12 -> 472
    //   463: ldc_w 403
    //   466: aload_1
    //   467: invokevirtual 395	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   470: istore 8
    //   472: ldc 116
    //   474: iconst_3
    //   475: invokestatic 407	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   478: ifeq +48 -> 526
    //   481: aload 9
    //   483: invokevirtual 411	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   486: invokestatic 188	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   489: astore_1
    //   490: ldc 116
    //   492: new 184	java/lang/StringBuilder
    //   495: dup
    //   496: aload_1
    //   497: invokestatic 188	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   500: invokevirtual 192	java/lang/String:length	()I
    //   503: bipush 8
    //   505: iadd
    //   506: invokespecial 195	java/lang/StringBuilder:<init>	(I)V
    //   509: ldc_w 413
    //   512: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   515: aload_1
    //   516: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   519: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   522: invokestatic 416	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   525: pop
    //   526: aload_0
    //   527: getfield 418	com/google/android/gms/iid/zzl:zzifw	Landroid/os/Messenger;
    //   530: ifnull +105 -> 635
    //   533: aload 9
    //   535: ldc_w 420
    //   538: aload_0
    //   539: getfield 242	com/google/android/gms/iid/zzl:zzicr	Landroid/os/Messenger;
    //   542: invokevirtual 423	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   545: pop
    //   546: invokestatic 429	android/os/Message:obtain	()Landroid/os/Message;
    //   549: astore_1
    //   550: aload_1
    //   551: aload 9
    //   553: putfield 433	android/os/Message:obj	Ljava/lang/Object;
    //   556: aload_0
    //   557: getfield 418	com/google/android/gms/iid/zzl:zzifw	Landroid/os/Messenger;
    //   560: aload_1
    //   561: invokevirtual 437	android/os/Messenger:send	(Landroid/os/Message;)V
    //   564: aload 11
    //   566: ldc2_w 438
    //   569: invokevirtual 443	android/os/ConditionVariable:block	(J)Z
    //   572: pop
    //   573: aload_0
    //   574: invokevirtual 218	java/lang/Object:getClass	()Ljava/lang/Class;
    //   577: astore_1
    //   578: aload_1
    //   579: monitorenter
    //   580: aload_0
    //   581: getfield 56	com/google/android/gms/iid/zzl:zzifv	Ljava/util/Map;
    //   584: aload 10
    //   586: invokeinterface 446 2 0
    //   591: astore_2
    //   592: aload_2
    //   593: instanceof 131
    //   596: ifeq +240 -> 836
    //   599: aload_2
    //   600: checkcast 131	android/content/Intent
    //   603: astore_2
    //   604: aload_1
    //   605: monitorexit
    //   606: aload_2
    //   607: areturn
    //   608: ldc_w 448
    //   611: astore 9
    //   613: goto -441 -> 172
    //   616: astore_1
    //   617: ldc 116
    //   619: iconst_3
    //   620: invokestatic 407	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   623: ifeq +12 -> 635
    //   626: ldc 116
    //   628: ldc_w 450
    //   631: invokestatic 416	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   634: pop
    //   635: iload 8
    //   637: ifeq +96 -> 733
    //   640: aload_0
    //   641: monitorenter
    //   642: getstatic 46	com/google/android/gms/iid/zzl:zzifu	Landroid/content/BroadcastReceiver;
    //   645: ifnonnull +69 -> 714
    //   648: new 452	com/google/android/gms/iid/zzn
    //   651: dup
    //   652: aload_0
    //   653: invokespecial 455	com/google/android/gms/iid/zzn:<init>	(Lcom/google/android/gms/iid/zzl;)V
    //   656: putstatic 46	com/google/android/gms/iid/zzl:zzifu	Landroid/content/BroadcastReceiver;
    //   659: ldc 116
    //   661: iconst_3
    //   662: invokestatic 407	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   665: ifeq +12 -> 677
    //   668: ldc 116
    //   670: ldc_w 457
    //   673: invokestatic 416	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   676: pop
    //   677: new 459	android/content/IntentFilter
    //   680: dup
    //   681: ldc_w 461
    //   684: invokespecial 462	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   687: astore_1
    //   688: aload_1
    //   689: aload_0
    //   690: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   693: invokevirtual 367	android/content/Context:getPackageName	()Ljava/lang/String;
    //   696: invokevirtual 465	android/content/IntentFilter:addCategory	(Ljava/lang/String;)V
    //   699: aload_0
    //   700: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   703: getstatic 46	com/google/android/gms/iid/zzl:zzifu	Landroid/content/BroadcastReceiver;
    //   706: aload_1
    //   707: ldc -82
    //   709: aconst_null
    //   710: invokevirtual 469	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   713: pop
    //   714: aload_0
    //   715: monitorexit
    //   716: aload_0
    //   717: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   720: aload 9
    //   722: invokevirtual 472	android/content/Context:sendBroadcast	(Landroid/content/Intent;)V
    //   725: goto -161 -> 564
    //   728: astore_1
    //   729: aload_0
    //   730: monitorexit
    //   731: aload_1
    //   732: athrow
    //   733: aload 9
    //   735: ldc_w 420
    //   738: aload_0
    //   739: getfield 242	com/google/android/gms/iid/zzl:zzicr	Landroid/os/Messenger;
    //   742: invokevirtual 423	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   745: pop
    //   746: aload 9
    //   748: ldc_w 474
    //   751: ldc_w 403
    //   754: invokevirtual 387	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   757: pop
    //   758: aload_0
    //   759: getfield 476	com/google/android/gms/iid/zzl:zzifx	Lcom/google/android/gms/iid/MessengerCompat;
    //   762: ifnull +43 -> 805
    //   765: invokestatic 429	android/os/Message:obtain	()Landroid/os/Message;
    //   768: astore_1
    //   769: aload_1
    //   770: aload 9
    //   772: putfield 433	android/os/Message:obj	Ljava/lang/Object;
    //   775: aload_0
    //   776: getfield 476	com/google/android/gms/iid/zzl:zzifx	Lcom/google/android/gms/iid/MessengerCompat;
    //   779: aload_1
    //   780: invokevirtual 479	com/google/android/gms/iid/MessengerCompat:send	(Landroid/os/Message;)V
    //   783: goto -219 -> 564
    //   786: astore_1
    //   787: ldc 116
    //   789: iconst_3
    //   790: invokestatic 407	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   793: ifeq +12 -> 805
    //   796: ldc 116
    //   798: ldc_w 450
    //   801: invokestatic 416	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   804: pop
    //   805: getstatic 38	com/google/android/gms/iid/zzl:zzifq	Z
    //   808: ifeq +15 -> 823
    //   811: aload_0
    //   812: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   815: aload 9
    //   817: invokevirtual 472	android/content/Context:sendBroadcast	(Landroid/content/Intent;)V
    //   820: goto -256 -> 564
    //   823: aload_0
    //   824: getfield 58	com/google/android/gms/iid/zzl:zzair	Landroid/content/Context;
    //   827: aload 9
    //   829: invokevirtual 483	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   832: pop
    //   833: goto -269 -> 564
    //   836: aload_2
    //   837: instanceof 76
    //   840: ifeq +20 -> 860
    //   843: new 271	java/io/IOException
    //   846: dup
    //   847: aload_2
    //   848: checkcast 76	java/lang/String
    //   851: invokespecial 301	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   854: athrow
    //   855: astore_2
    //   856: aload_1
    //   857: monitorexit
    //   858: aload_2
    //   859: athrow
    //   860: aload_2
    //   861: invokestatic 188	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   864: astore_2
    //   865: ldc 116
    //   867: new 184	java/lang/StringBuilder
    //   870: dup
    //   871: aload_2
    //   872: invokestatic 188	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   875: invokevirtual 192	java/lang/String:length	()I
    //   878: bipush 12
    //   880: iadd
    //   881: invokespecial 195	java/lang/StringBuilder:<init>	(I)V
    //   884: ldc_w 485
    //   887: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   890: aload_2
    //   891: invokevirtual 201	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   894: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   897: invokestatic 212	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   900: pop
    //   901: new 271	java/io/IOException
    //   904: dup
    //   905: ldc_w 487
    //   908: invokespecial 301	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   911: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	912	0	this	zzl
    //   0	912	2	paramKeyPair	KeyPair
    //   74	33	3	i	int
    //   43	52	4	l1	long
    //   68	25	6	l2	long
    //   448	188	8	bool	boolean
    //   18	810	9	localObject	Object
    //   12	573	10	str1	String
    //   7	558	11	localConditionVariable	ConditionVariable
    //   286	33	12	str2	String
    // Exception table:
    //   from	to	target	type
    //   23	40	128	finally
    //   129	132	128	finally
    //   556	564	616	android/os/RemoteException
    //   642	677	728	finally
    //   677	714	728	finally
    //   714	716	728	finally
    //   729	731	728	finally
    //   775	783	786	android/os/RemoteException
    //   580	606	855	finally
    //   836	855	855	finally
    //   856	858	855	finally
    //   860	912	855	finally
  }
  
  private static boolean zzb(PackageManager paramPackageManager, String paramString)
  {
    try
    {
      paramPackageManager = paramPackageManager.getApplicationInfo(paramString, 0);
      zzifp = paramPackageManager.packageName;
      zzifs = paramPackageManager.uid;
      return true;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager) {}
    return false;
  }
  
  public static String zzdp(Context paramContext)
  {
    if (zzifp != null) {
      return zzifp;
    }
    zzifr = Process.myUid();
    paramContext = paramContext.getPackageManager();
    if (!zzq.isAtLeastO())
    {
      Iterator localIterator = paramContext.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0).iterator();
      while (localIterator.hasNext()) {
        if (zza(paramContext, ((ResolveInfo)localIterator.next()).serviceInfo.packageName, "com.google.android.c2dm.intent.REGISTER")) {
          zzifq = false;
        }
      }
      for (int i = 1; i != 0; i = 0) {
        return zzifp;
      }
    }
    if (zza(paramContext)) {
      return zzifp;
    }
    Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
    if (zzb(paramContext, "com.google.android.gms"))
    {
      zzifq = zzq.isAtLeastO();
      return zzifp;
    }
    if ((!zzq.zzamn()) && (zzb(paramContext, "com.google.android.gsf")))
    {
      zzifq = false;
      return zzifp;
    }
    Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
    return null;
  }
  
  private static int zzdq(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      int i = localPackageManager.getPackageInfo(zzdp(paramContext), 0).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return -1;
  }
  
  private static void zze(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 instanceof ConditionVariable)) {
      ((ConditionVariable)paramObject1).open();
    }
    Message localMessage;
    if ((paramObject1 instanceof Messenger))
    {
      paramObject1 = (Messenger)paramObject1;
      localMessage = Message.obtain();
      localMessage.obj = paramObject2;
    }
    try
    {
      ((Messenger)paramObject1).send(localMessage);
      return;
    }
    catch (RemoteException paramObject1)
    {
      paramObject1 = String.valueOf(paramObject1);
      Log.w("InstanceID/Rpc", String.valueOf(paramObject1).length() + 24 + "Failed to send response " + (String)paramObject1);
    }
  }
  
  private final void zzi(Intent paramIntent)
  {
    try
    {
      if (this.zzicn == null)
      {
        Intent localIntent = new Intent();
        localIntent.setPackage("com.google.example.invalidpackage");
        this.zzicn = PendingIntent.getBroadcast(this.zzair, 0, localIntent, 0);
      }
      paramIntent.putExtra("app", this.zzicn);
      return;
    }
    finally {}
  }
  
  private final void zzi(String paramString, Object paramObject)
  {
    synchronized (getClass())
    {
      Object localObject = this.zzifv.get(paramString);
      this.zzifv.put(paramString, paramObject);
      zze(localObject, paramObject);
      return;
    }
  }
  
  static String zzj(Intent paramIntent)
    throws IOException
  {
    if (paramIntent == null) {
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    String str2 = paramIntent.getStringExtra("registration_id");
    String str1 = str2;
    if (str2 == null) {
      str1 = paramIntent.getStringExtra("unregistered");
    }
    paramIntent.getLongExtra("Retry-After", 0L);
    if (str1 == null)
    {
      str1 = paramIntent.getStringExtra("error");
      if (str1 != null) {
        throw new IOException(str1);
      }
      paramIntent = String.valueOf(paramIntent.getExtras());
      Log.w("InstanceID/Rpc", String.valueOf(paramIntent).length() + 29 + "Unexpected response from GCM " + paramIntent, new Throwable());
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    return str1;
  }
  
  final Intent zza(Bundle paramBundle, KeyPair paramKeyPair)
    throws IOException
  {
    Intent localIntent = zzb(paramBundle, paramKeyPair);
    Object localObject = localIntent;
    if (localIntent != null)
    {
      localObject = localIntent;
      if (localIntent.hasExtra("google.messenger"))
      {
        paramBundle = zzb(paramBundle, paramKeyPair);
        localObject = paramBundle;
        if (paramBundle != null)
        {
          localObject = paramBundle;
          if (paramBundle.hasExtra("google.messenger")) {
            localObject = null;
          }
        }
      }
    }
    return (Intent)localObject;
  }
  
  public final void zzc(Message paramMessage)
  {
    if (paramMessage == null) {
      return;
    }
    if ((paramMessage.obj instanceof Intent))
    {
      Object localObject = (Intent)paramMessage.obj;
      ((Intent)localObject).setExtrasClassLoader(MessengerCompat.class.getClassLoader());
      if (((Intent)localObject).hasExtra("google.messenger"))
      {
        localObject = ((Intent)localObject).getParcelableExtra("google.messenger");
        if ((localObject instanceof MessengerCompat)) {
          this.zzifx = ((MessengerCompat)localObject);
        }
        if ((localObject instanceof Messenger)) {
          this.zzifw = ((Messenger)localObject);
        }
      }
      zzk((Intent)paramMessage.obj);
      return;
    }
    Log.w("InstanceID/Rpc", "Dropping invalid message");
  }
  
  public final void zzk(Intent paramIntent)
  {
    if (paramIntent == null) {
      if (Log.isLoggable("InstanceID/Rpc", 3)) {
        Log.d("InstanceID/Rpc", "Unexpected response: null");
      }
    }
    do
    {
      return;
      localObject1 = paramIntent.getAction();
      if (("com.google.android.c2dm.intent.REGISTRATION".equals(localObject1)) || ("com.google.android.gms.iid.InstanceID".equals(localObject1))) {
        break;
      }
    } while (!Log.isLoggable("InstanceID/Rpc", 3));
    paramIntent = String.valueOf(paramIntent.getAction());
    if (paramIntent.length() != 0) {}
    for (paramIntent = "Unexpected response ".concat(paramIntent);; paramIntent = new String("Unexpected response "))
    {
      Log.d("InstanceID/Rpc", paramIntent);
      return;
    }
    Object localObject1 = paramIntent.getStringExtra("registration_id");
    if (localObject1 == null) {
      localObject1 = paramIntent.getStringExtra("unregistered");
    }
    for (;;)
    {
      Object localObject2;
      Object localObject4;
      label293:
      Object localObject3;
      if (localObject1 == null)
      {
        localObject2 = paramIntent.getStringExtra("error");
        if (localObject2 == null)
        {
          paramIntent = String.valueOf(paramIntent.getExtras());
          Log.w("InstanceID/Rpc", String.valueOf(paramIntent).length() + 49 + "Unexpected response, no error or registration id " + paramIntent);
          return;
        }
        if (Log.isLoggable("InstanceID/Rpc", 3))
        {
          localObject1 = String.valueOf(localObject2);
          if (((String)localObject1).length() != 0)
          {
            localObject1 = "Received InstanceID error ".concat((String)localObject1);
            Log.d("InstanceID/Rpc", (String)localObject1);
          }
        }
        else
        {
          if (!((String)localObject2).startsWith("|")) {
            break label954;
          }
          localObject4 = ((String)localObject2).split("\\|");
          if (!"ID".equals(localObject4[1]))
          {
            localObject1 = String.valueOf(localObject2);
            if (((String)localObject1).length() == 0) {
              break label466;
            }
            localObject1 = "Unexpected structured response ".concat((String)localObject1);
            Log.w("InstanceID/Rpc", (String)localObject1);
          }
          if (localObject4.length <= 2) {
            break label481;
          }
          localObject3 = localObject4[2];
          localObject4 = localObject4[3];
          localObject2 = localObject3;
          localObject1 = localObject4;
          if (((String)localObject4).startsWith(":"))
          {
            localObject1 = ((String)localObject4).substring(1);
            localObject2 = localObject3;
          }
          label351:
          paramIntent.putExtra("error", (String)localObject1);
        }
      }
      for (;;)
      {
        if (localObject2 == null) {
          zzae(localObject1);
        }
        for (;;)
        {
          long l = paramIntent.getLongExtra("Retry-After", 0L);
          if (l <= 0L) {
            break label503;
          }
          this.zzifz = SystemClock.elapsedRealtime();
          this.zzigb = ((int)l * 1000);
          this.zzigc = (SystemClock.elapsedRealtime() + this.zzigb);
          i = this.zzigb;
          Log.w("InstanceID/Rpc", 52 + "Explicit request from server to backoff: " + i);
          return;
          localObject1 = new String("Received InstanceID error ");
          break;
          label466:
          localObject1 = new String("Unexpected structured response ");
          break label293;
          label481:
          localObject1 = "UNKNOWN";
          localObject2 = null;
          break label351;
          zzi((String)localObject2, localObject1);
        }
        label503:
        if (((!"SERVICE_NOT_AVAILABLE".equals(localObject1)) && (!"AUTHENTICATION_FAILED".equals(localObject1))) || (!"com.google.android.gsf".equals(zzifp))) {
          break;
        }
        this.zziga += 1;
        if (this.zziga < 3) {
          break;
        }
        if (this.zziga == 3) {
          this.zzigb = (new Random().nextInt(1000) + 1000);
        }
        this.zzigb <<= 1;
        this.zzigc = (SystemClock.elapsedRealtime() + this.zzigb);
        int i = this.zzigb;
        Log.w("InstanceID/Rpc", String.valueOf(localObject1).length() + 31 + "Backoff due to " + (String)localObject1 + " for " + i);
        return;
        this.zzify = SystemClock.elapsedRealtime();
        this.zzigc = 0L;
        this.zziga = 0;
        this.zzigb = 0;
        localObject2 = null;
        if (((String)localObject1).startsWith("|"))
        {
          localObject3 = ((String)localObject1).split("\\|");
          if (!"ID".equals(localObject3[1]))
          {
            localObject1 = String.valueOf(localObject1);
            if (((String)localObject1).length() == 0) {
              break label881;
            }
            localObject1 = "Unexpected structured response ".concat((String)localObject1);
            Log.w("InstanceID/Rpc", (String)localObject1);
          }
          localObject2 = localObject3[2];
          if (localObject3.length > 4)
          {
            if (!"SYNC".equals(localObject3[3])) {
              break label896;
            }
            localObject1 = this.zzair;
            localObject4 = new Intent("com.google.android.gms.iid.InstanceID");
            ((Intent)localObject4).putExtra("CMD", "SYNC");
            ((Intent)localObject4).setClassName((Context)localObject1, "com.google.android.gms.gcm.GcmReceiver");
            ((Context)localObject1).sendBroadcast((Intent)localObject4);
          }
        }
        label881:
        label896:
        while (!"RST".equals(localObject3[3]))
        {
          localObject3 = localObject3[(localObject3.length - 1)];
          localObject1 = localObject3;
          if (((String)localObject3).startsWith(":")) {
            localObject1 = ((String)localObject3).substring(1);
          }
          paramIntent.putExtra("registration_id", (String)localObject1);
          if (localObject2 != null) {
            break label946;
          }
          zzae(paramIntent);
          return;
          localObject1 = new String("Unexpected structured response ");
          break;
        }
        localObject1 = this.zzair;
        InstanceID.getInstance(this.zzair);
        InstanceIDListenerService.zza((Context)localObject1, InstanceID.zzavg());
        paramIntent.removeExtra("registration_id");
        zzi((String)localObject2, paramIntent);
        return;
        label946:
        zzi((String)localObject2, paramIntent);
        return;
        label954:
        localObject3 = null;
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */