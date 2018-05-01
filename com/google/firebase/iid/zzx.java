package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.concurrent.GuardedBy;

final class zzx
{
  private static int zzbrh = 0;
  private static PendingIntent zzbro;
  private final zzw zzbqn;
  @GuardedBy("responseCallbacks")
  private final SimpleArrayMap<String, TaskCompletionSource<Bundle>> zzbrp = new SimpleArrayMap();
  private Messenger zzbrq;
  private Messenger zzbrr;
  private zzi zzbrs;
  private final Context zzqs;
  
  public zzx(Context paramContext, zzw paramzzw)
  {
    this.zzqs = paramContext;
    this.zzbqn = paramzzw;
    this.zzbrq = new Messenger(new zzy(this, Looper.getMainLooper()));
  }
  
  private static void zza(Context paramContext, Intent paramIntent)
  {
    try
    {
      if (zzbro == null)
      {
        Intent localIntent = new android/content/Intent;
        localIntent.<init>();
        localIntent.setPackage("com.google.example.invalidpackage");
        zzbro = PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
      }
      paramIntent.putExtra("app", zzbro);
      return;
    }
    finally {}
  }
  
  private final void zza(String paramString, Bundle paramBundle)
  {
    for (;;)
    {
      TaskCompletionSource localTaskCompletionSource;
      synchronized (this.zzbrp)
      {
        localTaskCompletionSource = (TaskCompletionSource)this.zzbrp.remove(paramString);
        if (localTaskCompletionSource == null)
        {
          paramString = String.valueOf(paramString);
          if (paramString.length() != 0)
          {
            paramString = "Missing callback for ".concat(paramString);
            Log.w("FirebaseInstanceId", paramString);
            return;
          }
          paramString = new String("Missing callback for ");
        }
      }
      localTaskCompletionSource.setResult(paramBundle);
    }
  }
  
  private final void zzb(Message paramMessage)
  {
    Object localObject1;
    Object localObject4;
    if ((paramMessage != null) && ((paramMessage.obj instanceof Intent)))
    {
      localObject1 = (Intent)paramMessage.obj;
      ((Intent)localObject1).setExtrasClassLoader(new zzi.zza());
      if (((Intent)localObject1).hasExtra("google.messenger"))
      {
        localObject1 = ((Intent)localObject1).getParcelableExtra("google.messenger");
        if ((localObject1 instanceof zzi)) {
          this.zzbrs = ((zzi)localObject1);
        }
        if ((localObject1 instanceof Messenger)) {
          this.zzbrr = ((Messenger)localObject1);
        }
      }
      localObject4 = (Intent)paramMessage.obj;
      paramMessage = ((Intent)localObject4).getAction();
      if (!"com.google.android.c2dm.intent.REGISTRATION".equals(paramMessage)) {
        if (Log.isLoggable("FirebaseInstanceId", 3))
        {
          paramMessage = String.valueOf(paramMessage);
          if (paramMessage.length() == 0) {
            break label137;
          }
          paramMessage = "Unexpected response action: ".concat(paramMessage);
          Log.d("FirebaseInstanceId", paramMessage);
        }
      }
    }
    for (;;)
    {
      return;
      label137:
      paramMessage = new String("Unexpected response action: ");
      break;
      localObject1 = ((Intent)localObject4).getStringExtra("registration_id");
      paramMessage = (Message)localObject1;
      if (localObject1 == null) {
        paramMessage = ((Intent)localObject4).getStringExtra("unregistered");
      }
      if (paramMessage == null)
      {
        localObject1 = ((Intent)localObject4).getStringExtra("error");
        if (localObject1 == null)
        {
          paramMessage = String.valueOf(((Intent)localObject4).getExtras());
          Log.w("FirebaseInstanceId", String.valueOf(paramMessage).length() + 49 + "Unexpected response, no error or registration id " + paramMessage);
        }
        else
        {
          if (Log.isLoggable("FirebaseInstanceId", 3))
          {
            paramMessage = String.valueOf(localObject1);
            if (paramMessage.length() != 0)
            {
              paramMessage = "Received InstanceID error ".concat(paramMessage);
              label259:
              Log.d("FirebaseInstanceId", paramMessage);
            }
          }
          else
          {
            if (!((String)localObject1).startsWith("|")) {
              break label399;
            }
            paramMessage = ((String)localObject1).split("\\|");
            if ((paramMessage.length > 2) && ("ID".equals(paramMessage[1]))) {
              break label354;
            }
            paramMessage = String.valueOf(localObject1);
            if (paramMessage.length() == 0) {
              break label341;
            }
          }
          label341:
          for (paramMessage = "Unexpected structured response ".concat(paramMessage);; paramMessage = new String("Unexpected structured response "))
          {
            Log.w("FirebaseInstanceId", paramMessage);
            break;
            paramMessage = new String("Received InstanceID error ");
            break label259;
          }
          label354:
          String str = paramMessage[2];
          localObject1 = paramMessage[3];
          paramMessage = (Message)localObject1;
          if (((String)localObject1).startsWith(":")) {
            paramMessage = ((String)localObject1).substring(1);
          }
          zza(str, ((Intent)localObject4).putExtra("error", paramMessage).getExtras());
          continue;
          label399:
          paramMessage = this.zzbrp;
          int i = 0;
          try
          {
            while (i < this.zzbrp.size())
            {
              zza((String)this.zzbrp.keyAt(i), ((Intent)localObject4).getExtras());
              i++;
            }
            continue;
          }
          finally {}
        }
      }
      else
      {
        Object localObject3 = Pattern.compile("\\|ID\\|([^|]+)\\|:?+(.*)").matcher(paramMessage);
        if (!((Matcher)localObject3).matches())
        {
          if (Log.isLoggable("FirebaseInstanceId", 3))
          {
            paramMessage = String.valueOf(paramMessage);
            if (paramMessage.length() != 0) {}
            for (paramMessage = "Unexpected response string: ".concat(paramMessage);; paramMessage = new String("Unexpected response string: "))
            {
              Log.d("FirebaseInstanceId", paramMessage);
              break;
            }
          }
        }
        else
        {
          paramMessage = ((Matcher)localObject3).group(1);
          localObject3 = ((Matcher)localObject3).group(2);
          localObject4 = ((Intent)localObject4).getExtras();
          ((Bundle)localObject4).putString("registration_id", (String)localObject3);
          zza(paramMessage, (Bundle)localObject4);
          continue;
          Log.w("FirebaseInstanceId", "Dropping invalid message");
        }
      }
    }
  }
  
  private final Bundle zzj(Bundle paramBundle)
    throws IOException
  {
    Bundle localBundle1 = zzk(paramBundle);
    Bundle localBundle2 = localBundle1;
    if (localBundle1 != null)
    {
      localBundle2 = localBundle1;
      if (localBundle1.containsKey("google.messenger"))
      {
        paramBundle = zzk(paramBundle);
        localBundle2 = paramBundle;
        if (paramBundle != null)
        {
          localBundle2 = paramBundle;
          if (paramBundle.containsKey("google.messenger")) {
            localBundle2 = null;
          }
        }
      }
    }
    return localBundle2;
  }
  
  /* Error */
  private final Bundle zzk(Bundle arg1)
    throws IOException
  {
    // Byte code:
    //   0: invokestatic 290	com/google/firebase/iid/zzx:zzsz	()Ljava/lang/String;
    //   3: astore_2
    //   4: new 96	com/google/android/gms/tasks/TaskCompletionSource
    //   7: dup
    //   8: invokespecial 291	com/google/android/gms/tasks/TaskCompletionSource:<init>	()V
    //   11: astore_3
    //   12: aload_0
    //   13: getfield 37	com/google/firebase/iid/zzx:zzbrp	Landroid/support/v4/util/SimpleArrayMap;
    //   16: astore 4
    //   18: aload 4
    //   20: monitorenter
    //   21: aload_0
    //   22: getfield 37	com/google/firebase/iid/zzx:zzbrp	Landroid/support/v4/util/SimpleArrayMap;
    //   25: aload_2
    //   26: aload_3
    //   27: invokevirtual 295	android/support/v4/util/SimpleArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   30: pop
    //   31: aload 4
    //   33: monitorexit
    //   34: aload_0
    //   35: getfield 41	com/google/firebase/iid/zzx:zzbqn	Lcom/google/firebase/iid/zzw;
    //   38: invokevirtual 300	com/google/firebase/iid/zzw:zzsu	()I
    //   41: ifne +20 -> 61
    //   44: new 272	java/io/IOException
    //   47: dup
    //   48: ldc_w 302
    //   51: invokespecial 303	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   54: athrow
    //   55: astore_1
    //   56: aload 4
    //   58: monitorexit
    //   59: aload_1
    //   60: athrow
    //   61: new 65	android/content/Intent
    //   64: dup
    //   65: invokespecial 66	android/content/Intent:<init>	()V
    //   68: astore 4
    //   70: aload 4
    //   72: ldc_w 305
    //   75: invokevirtual 72	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
    //   78: pop
    //   79: aload_0
    //   80: getfield 41	com/google/firebase/iid/zzx:zzbqn	Lcom/google/firebase/iid/zzw;
    //   83: invokevirtual 300	com/google/firebase/iid/zzw:zzsu	()I
    //   86: iconst_2
    //   87: if_icmpne +213 -> 300
    //   90: aload 4
    //   92: ldc_w 307
    //   95: invokevirtual 310	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   98: pop
    //   99: aload 4
    //   101: aload_1
    //   102: invokevirtual 314	android/content/Intent:putExtras	(Landroid/os/Bundle;)Landroid/content/Intent;
    //   105: pop
    //   106: aload_0
    //   107: getfield 39	com/google/firebase/iid/zzx:zzqs	Landroid/content/Context;
    //   110: aload 4
    //   112: invokestatic 316	com/google/firebase/iid/zzx:zza	(Landroid/content/Context;Landroid/content/Intent;)V
    //   115: aload 4
    //   117: ldc_w 318
    //   120: new 190	java/lang/StringBuilder
    //   123: dup
    //   124: aload_2
    //   125: invokestatic 102	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   128: invokevirtual 106	java/lang/String:length	()I
    //   131: iconst_5
    //   132: iadd
    //   133: invokespecial 193	java/lang/StringBuilder:<init>	(I)V
    //   136: ldc_w 320
    //   139: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_2
    //   143: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: ldc -50
    //   148: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: invokevirtual 202	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   154: invokevirtual 228	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   157: pop
    //   158: ldc 114
    //   160: iconst_3
    //   161: invokestatic 170	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   164: ifeq +48 -> 212
    //   167: aload 4
    //   169: invokevirtual 188	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   172: invokestatic 102	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   175: astore_1
    //   176: ldc 114
    //   178: new 190	java/lang/StringBuilder
    //   181: dup
    //   182: aload_1
    //   183: invokestatic 102	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   186: invokevirtual 106	java/lang/String:length	()I
    //   189: bipush 8
    //   191: iadd
    //   192: invokespecial 193	java/lang/StringBuilder:<init>	(I)V
    //   195: ldc_w 322
    //   198: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload_1
    //   202: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: invokevirtual 202	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   208: invokestatic 175	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   211: pop
    //   212: aload 4
    //   214: ldc -114
    //   216: aload_0
    //   217: getfield 59	com/google/firebase/iid/zzx:zzbrq	Landroid/os/Messenger;
    //   220: invokevirtual 84	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   223: pop
    //   224: aload_0
    //   225: getfield 156	com/google/firebase/iid/zzx:zzbrr	Landroid/os/Messenger;
    //   228: ifnonnull +10 -> 238
    //   231: aload_0
    //   232: getfield 154	com/google/firebase/iid/zzx:zzbrs	Lcom/google/firebase/iid/zzi;
    //   235: ifnull +107 -> 342
    //   238: invokestatic 326	android/os/Message:obtain	()Landroid/os/Message;
    //   241: astore_1
    //   242: aload_1
    //   243: aload 4
    //   245: putfield 133	android/os/Message:obj	Ljava/lang/Object;
    //   248: aload_0
    //   249: getfield 156	com/google/firebase/iid/zzx:zzbrr	Landroid/os/Messenger;
    //   252: ifnull +60 -> 312
    //   255: aload_0
    //   256: getfield 156	com/google/firebase/iid/zzx:zzbrr	Landroid/os/Messenger;
    //   259: aload_1
    //   260: invokevirtual 329	android/os/Messenger:send	(Landroid/os/Message;)V
    //   263: aload_3
    //   264: invokevirtual 333	com/google/android/gms/tasks/TaskCompletionSource:getTask	()Lcom/google/android/gms/tasks/Task;
    //   267: ldc2_w 334
    //   270: getstatic 341	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   273: invokestatic 347	com/google/android/gms/tasks/Tasks:await	(Lcom/google/android/gms/tasks/Task;JLjava/util/concurrent/TimeUnit;)Ljava/lang/Object;
    //   276: checkcast 262	android/os/Bundle
    //   279: astore_3
    //   280: aload_0
    //   281: getfield 37	com/google/firebase/iid/zzx:zzbrp	Landroid/support/v4/util/SimpleArrayMap;
    //   284: astore_1
    //   285: aload_1
    //   286: monitorenter
    //   287: aload_0
    //   288: getfield 37	com/google/firebase/iid/zzx:zzbrp	Landroid/support/v4/util/SimpleArrayMap;
    //   291: aload_2
    //   292: invokevirtual 94	android/support/v4/util/SimpleArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   295: pop
    //   296: aload_1
    //   297: monitorexit
    //   298: aload_3
    //   299: areturn
    //   300: aload 4
    //   302: ldc_w 349
    //   305: invokevirtual 310	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   308: pop
    //   309: goto -210 -> 99
    //   312: aload_0
    //   313: getfield 154	com/google/firebase/iid/zzx:zzbrs	Lcom/google/firebase/iid/zzi;
    //   316: aload_1
    //   317: invokevirtual 350	com/google/firebase/iid/zzi:send	(Landroid/os/Message;)V
    //   320: goto -57 -> 263
    //   323: astore_1
    //   324: ldc 114
    //   326: iconst_3
    //   327: invokestatic 170	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   330: ifeq +12 -> 342
    //   333: ldc 114
    //   335: ldc_w 352
    //   338: invokestatic 175	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   341: pop
    //   342: aload_0
    //   343: getfield 41	com/google/firebase/iid/zzx:zzbqn	Lcom/google/firebase/iid/zzw;
    //   346: invokevirtual 300	com/google/firebase/iid/zzw:zzsu	()I
    //   349: iconst_2
    //   350: if_icmpne +15 -> 365
    //   353: aload_0
    //   354: getfield 39	com/google/firebase/iid/zzx:zzqs	Landroid/content/Context;
    //   357: aload 4
    //   359: invokevirtual 358	android/content/Context:sendBroadcast	(Landroid/content/Intent;)V
    //   362: goto -99 -> 263
    //   365: aload_0
    //   366: getfield 39	com/google/firebase/iid/zzx:zzqs	Landroid/content/Context;
    //   369: aload 4
    //   371: invokevirtual 362	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   374: pop
    //   375: goto -112 -> 263
    //   378: astore_2
    //   379: aload_1
    //   380: monitorexit
    //   381: aload_2
    //   382: athrow
    //   383: astore_1
    //   384: ldc 114
    //   386: ldc_w 364
    //   389: invokestatic 120	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   392: pop
    //   393: new 272	java/io/IOException
    //   396: astore_1
    //   397: aload_1
    //   398: ldc_w 366
    //   401: invokespecial 303	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   404: aload_1
    //   405: athrow
    //   406: astore_3
    //   407: aload_0
    //   408: getfield 37	com/google/firebase/iid/zzx:zzbrp	Landroid/support/v4/util/SimpleArrayMap;
    //   411: astore_1
    //   412: aload_1
    //   413: monitorenter
    //   414: aload_0
    //   415: getfield 37	com/google/firebase/iid/zzx:zzbrp	Landroid/support/v4/util/SimpleArrayMap;
    //   418: aload_2
    //   419: invokevirtual 94	android/support/v4/util/SimpleArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   422: pop
    //   423: aload_1
    //   424: monitorexit
    //   425: aload_3
    //   426: athrow
    //   427: astore_1
    //   428: new 272	java/io/IOException
    //   431: astore_3
    //   432: aload_3
    //   433: aload_1
    //   434: invokespecial 369	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
    //   437: aload_3
    //   438: athrow
    //   439: astore_2
    //   440: aload_1
    //   441: monitorexit
    //   442: aload_2
    //   443: athrow
    //   444: astore_1
    //   445: goto -61 -> 384
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	448	0	this	zzx
    //   3	289	2	str	String
    //   378	41	2	localObject1	Object
    //   439	4	2	localObject2	Object
    //   11	288	3	localObject3	Object
    //   406	20	3	localObject4	Object
    //   431	7	3	localIOException	IOException
    //   16	354	4	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   21	34	55	finally
    //   56	59	55	finally
    //   248	263	323	android/os/RemoteException
    //   312	320	323	android/os/RemoteException
    //   287	298	378	finally
    //   379	381	378	finally
    //   263	280	383	java/lang/InterruptedException
    //   263	280	406	finally
    //   384	406	406	finally
    //   428	439	406	finally
    //   263	280	427	java/util/concurrent/ExecutionException
    //   414	425	439	finally
    //   440	442	439	finally
    //   263	280	444	java/util/concurrent/TimeoutException
  }
  
  private static String zzsz()
  {
    try
    {
      int i = zzbrh;
      zzbrh = i + 1;
      String str = Integer.toString(i);
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final Bundle zzi(Bundle paramBundle)
    throws IOException
  {
    Object localObject;
    if (this.zzbqn.zzsx() >= 12000000) {
      localObject = zzk.zzv(this.zzqs).zzb(1, paramBundle);
    }
    try
    {
      localObject = (Bundle)Tasks.await((Task)localObject);
      paramBundle = (Bundle)localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        if (Log.isLoggable("FirebaseInstanceId", 3))
        {
          String str = String.valueOf(localInterruptedException);
          Log.d("FirebaseInstanceId", String.valueOf(str).length() + 22 + "Error making request: " + str);
        }
        if (((localInterruptedException.getCause() instanceof zzu)) && (((zzu)localInterruptedException.getCause()).getErrorCode() == 4))
        {
          paramBundle = zzj(paramBundle);
        }
        else
        {
          paramBundle = null;
          continue;
          paramBundle = zzj(paramBundle);
        }
      }
    }
    catch (ExecutionException localExecutionException)
    {
      for (;;) {}
    }
    return paramBundle;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */