package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresPermission;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.zze;
import com.google.android.gms.iid.zzh;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GoogleCloudMessaging
{
  public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
  public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
  public static final String INSTANCE_ID_SCOPE = "GCM";
  @Deprecated
  public static final String MESSAGE_TYPE_DELETED = "deleted_messages";
  @Deprecated
  public static final String MESSAGE_TYPE_MESSAGE = "gcm";
  @Deprecated
  public static final String MESSAGE_TYPE_SEND_ERROR = "send_error";
  @Deprecated
  public static final String MESSAGE_TYPE_SEND_EVENT = "send_event";
  public static int zzbfL = 5000000;
  private static int zzbfM = 6500000;
  private static int zzbfN = 7000000;
  private static GoogleCloudMessaging zzbfO;
  private static final AtomicInteger zzbfR = new AtomicInteger(1);
  private PendingIntent zzbfP;
  private Map<String, Handler> zzbfQ = Collections.synchronizedMap(new HashMap());
  private final BlockingQueue<Intent> zzbfS = new LinkedBlockingQueue();
  private Messenger zzbfT = new Messenger(new zzc(this, Looper.getMainLooper()));
  private Context zzqD;
  
  public static GoogleCloudMessaging getInstance(Context paramContext)
  {
    try
    {
      if (zzbfO == null)
      {
        GoogleCloudMessaging localGoogleCloudMessaging = new GoogleCloudMessaging();
        zzbfO = localGoogleCloudMessaging;
        localGoogleCloudMessaging.zzqD = paramContext.getApplicationContext();
      }
      paramContext = zzbfO;
      return paramContext;
    }
    finally {}
  }
  
  @Deprecated
  private final Intent zza(Bundle paramBundle, boolean paramBoolean)
    throws IOException
  {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    if (zzaZ(this.zzqD) < 0) {
      throw new IOException("Google Play Services missing");
    }
    String str1;
    Intent localIntent;
    if (paramBoolean)
    {
      str1 = "com.google.iid.TOKEN_REQUEST";
      localIntent = new Intent(str1);
      localIntent.setPackage(zze.zzbd(this.zzqD));
      zzf(localIntent);
      str1 = String.valueOf("google.rpc");
      String str2 = String.valueOf(String.valueOf(zzbfR.getAndIncrement()));
      if (str2.length() == 0) {
        break label178;
      }
      str1 = str1.concat(str2);
      label110:
      localIntent.putExtra("google.message_id", str1);
      localIntent.putExtras(paramBundle);
      localIntent.putExtra("google.messenger", this.zzbfT);
      if (!paramBoolean) {
        break label190;
      }
      this.zzqD.sendBroadcast(localIntent);
    }
    for (;;)
    {
      try
      {
        paramBundle = (Intent)this.zzbfS.poll(30000L, TimeUnit.MILLISECONDS);
        return paramBundle;
      }
      catch (InterruptedException paramBundle)
      {
        label178:
        label190:
        throw new IOException(paramBundle.getMessage());
      }
      str1 = "com.google.android.c2dm.intent.REGISTER";
      break;
      str1 = new String(str1);
      break label110;
      this.zzqD.startService(localIntent);
    }
  }
  
  @Deprecated
  private final String zza(boolean paramBoolean, String... paramVarArgs)
    throws IOException
  {
    String str;
    try
    {
      str = zze.zzbd(this.zzqD);
      if (str == null) {
        throw new IOException("SERVICE_NOT_AVAILABLE");
      }
    }
    finally {}
    paramVarArgs = zzc(paramVarArgs);
    Object localObject = new Bundle();
    if (str.contains(".gsf"))
    {
      ((Bundle)localObject).putString("legacy.sender", paramVarArgs);
      paramVarArgs = InstanceID.getInstance(this.zzqD).getToken(paramVarArgs, "GCM", (Bundle)localObject);
    }
    do
    {
      return paramVarArgs;
      ((Bundle)localObject).putString("sender", paramVarArgs);
      localObject = zza((Bundle)localObject, paramBoolean);
      if (localObject == null) {
        throw new IOException("SERVICE_NOT_AVAILABLE");
      }
      str = ((Intent)localObject).getStringExtra("registration_id");
      paramVarArgs = str;
    } while (str != null);
    paramVarArgs = ((Intent)localObject).getStringExtra("error");
    if (paramVarArgs != null) {
      throw new IOException(paramVarArgs);
    }
    throw new IOException("SERVICE_NOT_AVAILABLE");
  }
  
  public static int zzaZ(Context paramContext)
  {
    String str = zze.zzbd(paramContext);
    if (str != null) {
      try
      {
        paramContext = paramContext.getPackageManager().getPackageInfo(str, 0);
        if (paramContext != null)
        {
          int i = paramContext.versionCode;
          return i;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext) {}
    }
    return -1;
  }
  
  private static String zzc(String... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      throw new IllegalArgumentException("No senderIds");
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs[0]);
    int i = 1;
    while (i < paramVarArgs.length)
    {
      localStringBuilder.append(',').append(paramVarArgs[i]);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private final boolean zze(Intent paramIntent)
  {
    Object localObject2 = paramIntent.getStringExtra("In-Reply-To");
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = localObject2;
      if (paramIntent.hasExtra("error")) {
        localObject1 = paramIntent.getStringExtra("google.message_id");
      }
    }
    if (localObject1 != null)
    {
      localObject1 = (Handler)this.zzbfQ.remove(localObject1);
      if (localObject1 != null)
      {
        localObject2 = Message.obtain();
        ((Message)localObject2).obj = paramIntent;
        return ((Handler)localObject1).sendMessage((Message)localObject2);
      }
    }
    return false;
  }
  
  private final void zzf(Intent paramIntent)
  {
    try
    {
      if (this.zzbfP == null)
      {
        Intent localIntent = new Intent();
        localIntent.setPackage("com.google.example.invalidpackage");
        this.zzbfP = PendingIntent.getBroadcast(this.zzqD, 0, localIntent, 0);
      }
      paramIntent.putExtra("app", this.zzbfP);
      return;
    }
    finally {}
  }
  
  private final void zzvD()
  {
    try
    {
      if (this.zzbfP != null)
      {
        this.zzbfP.cancel();
        this.zzbfP = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void close()
  {
    zzbfO = null;
    zza.zzbfw = null;
    zzvD();
  }
  
  public String getMessageType(Intent paramIntent)
  {
    if (!"com.google.android.c2dm.intent.RECEIVE".equals(paramIntent.getAction())) {
      paramIntent = null;
    }
    String str;
    do
    {
      return paramIntent;
      str = paramIntent.getStringExtra("message_type");
      paramIntent = str;
    } while (str != null);
    return "gcm";
  }
  
  @Deprecated
  @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
  public String register(String... paramVarArgs)
    throws IOException
  {
    try
    {
      paramVarArgs = zza(zze.zzbc(this.zzqD), paramVarArgs);
      return paramVarArgs;
    }
    finally
    {
      paramVarArgs = finally;
      throw paramVarArgs;
    }
  }
  
  @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
  public void send(String paramString1, String paramString2, long paramLong, Bundle paramBundle)
    throws IOException
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("Missing 'to'");
    }
    Object localObject1 = zze.zzbd(this.zzqD);
    if (localObject1 == null) {
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    Object localObject2 = new Intent("com.google.android.gcm.intent.SEND");
    if (paramBundle != null) {
      ((Intent)localObject2).putExtras(paramBundle);
    }
    zzf((Intent)localObject2);
    ((Intent)localObject2).setPackage((String)localObject1);
    ((Intent)localObject2).putExtra("google.to", paramString1);
    ((Intent)localObject2).putExtra("google.message_id", paramString2);
    ((Intent)localObject2).putExtra("google.ttl", Long.toString(paramLong));
    ((Intent)localObject2).putExtra("google.delay", Integer.toString(-1));
    int i = paramString1.indexOf('@');
    label206:
    Object localObject3;
    if (i > 0)
    {
      str = paramString1.substring(0, i);
      InstanceID.getInstance(this.zzqD);
      ((Intent)localObject2).putExtra("google.from", InstanceID.zzvM().zzf("", str, "GCM"));
      if (!((String)localObject1).contains(".gsf")) {
        break label342;
      }
      localObject1 = new Bundle();
      localObject2 = paramBundle.keySet().iterator();
      do
      {
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        str = (String)((Iterator)localObject2).next();
        localObject3 = paramBundle.get(str);
      } while (!(localObject3 instanceof String));
      str = String.valueOf(str);
      if (str.length() == 0) {
        break label291;
      }
    }
    label291:
    for (String str = "gcm.".concat(str);; str = new String("gcm."))
    {
      ((Bundle)localObject1).putString(str, (String)localObject3);
      break label206;
      str = paramString1;
      break;
    }
    ((Bundle)localObject1).putString("google.to", paramString1);
    ((Bundle)localObject1).putString("google.message_id", paramString2);
    InstanceID.getInstance(this.zzqD).zzc("GCM", "upstream", (Bundle)localObject1);
    return;
    label342:
    this.zzqD.sendOrderedBroadcast((Intent)localObject2, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
  }
  
  @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
  public void send(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    send(paramString1, paramString2, -1L, paramBundle);
  }
  
  @Deprecated
  @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
  public void unregister()
    throws IOException
  {
    try
    {
      if (Looper.getMainLooper() == Looper.myLooper()) {
        throw new IOException("MAIN_THREAD");
      }
    }
    finally {}
    InstanceID.getInstance(this.zzqD).deleteInstanceID();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GoogleCloudMessaging.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */