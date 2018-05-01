package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

public class FirebaseInstanceId
{
  private static final long zzbqi = TimeUnit.HOURS.toSeconds(8L);
  private static Map<String, FirebaseInstanceId> zzbqj = new ArrayMap();
  private static zzaa zzbqk;
  @GuardedBy("FirebaseInstanceId.class")
  private static ScheduledThreadPoolExecutor zzbql;
  private final FirebaseApp zzbqm;
  private final zzw zzbqn;
  private final zzx zzbqo;
  @GuardedBy("this")
  private KeyPair zzbqp;
  @GuardedBy("this")
  private boolean zzbqq = false;
  @GuardedBy("this")
  private boolean zzbqr;
  
  private FirebaseInstanceId(FirebaseApp paramFirebaseApp, zzw paramzzw)
  {
    if (zzw.zza(paramFirebaseApp) == null) {
      throw new IllegalStateException("FirebaseInstanceId failed to initialize, FirebaseApp is missing project ID");
    }
    this.zzbqm = paramFirebaseApp;
    this.zzbqn = paramzzw;
    this.zzbqo = new zzx(paramFirebaseApp.getApplicationContext(), paramzzw);
    this.zzbqr = zzsm();
    if (zzso()) {
      zzse();
    }
  }
  
  public static FirebaseInstanceId getInstance()
  {
    return getInstance(FirebaseApp.getInstance());
  }
  
  @Keep
  public static FirebaseInstanceId getInstance(FirebaseApp paramFirebaseApp)
  {
    try
    {
      Object localObject1 = (FirebaseInstanceId)zzbqj.get(paramFirebaseApp.getOptions().getApplicationId());
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        if (zzbqk == null)
        {
          localObject2 = new com/google/firebase/iid/zzaa;
          ((zzaa)localObject2).<init>(paramFirebaseApp.getApplicationContext());
          zzbqk = (zzaa)localObject2;
        }
        localObject2 = new com/google/firebase/iid/FirebaseInstanceId;
        localObject1 = new com/google/firebase/iid/zzw;
        ((zzw)localObject1).<init>(paramFirebaseApp.getApplicationContext());
        ((FirebaseInstanceId)localObject2).<init>(paramFirebaseApp, (zzw)localObject1);
        zzbqj.put(paramFirebaseApp.getOptions().getApplicationId(), localObject2);
      }
      return (FirebaseInstanceId)localObject2;
    }
    finally {}
  }
  
  private final KeyPair getKeyPair()
  {
    try
    {
      if (this.zzbqp == null) {
        this.zzbqp = zzbqk.zzfd("");
      }
      if (this.zzbqp == null) {
        this.zzbqp = zzbqk.zzfb("");
      }
      KeyPair localKeyPair = this.zzbqp;
      return localKeyPair;
    }
    finally {}
  }
  
  private final void startSync()
  {
    try
    {
      if (!this.zzbqq) {
        zzan(0L);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  static void zza(Runnable paramRunnable, long paramLong)
  {
    try
    {
      if (zzbql == null)
      {
        ScheduledThreadPoolExecutor localScheduledThreadPoolExecutor = new java/util/concurrent/ScheduledThreadPoolExecutor;
        localScheduledThreadPoolExecutor.<init>(1);
        zzbql = localScheduledThreadPoolExecutor;
      }
      zzbql.schedule(paramRunnable, paramLong, TimeUnit.SECONDS);
      return;
    }
    finally {}
  }
  
  private final String zzb(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    paramBundle.putString("scope", paramString2);
    paramBundle.putString("sender", paramString1);
    paramBundle.putString("subtype", paramString1);
    paramBundle.putString("appid", getId());
    paramBundle.putString("gmp_app_id", this.zzbqm.getOptions().getApplicationId());
    paramBundle.putString("gmsv", Integer.toString(this.zzbqn.zzsx()));
    paramBundle.putString("osv", Integer.toString(Build.VERSION.SDK_INT));
    paramBundle.putString("app_ver", this.zzbqn.zzsv());
    paramBundle.putString("app_ver_name", this.zzbqn.zzsw());
    paramBundle.putString("cliv", "fiid-12451000");
    paramBundle = this.zzbqo.zzi(paramBundle);
    if (paramBundle == null) {
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    paramString1 = paramBundle.getString("registration_id");
    if (paramString1 != null) {}
    do
    {
      return paramString1;
      paramString2 = paramBundle.getString("unregistered");
      paramString1 = paramString2;
    } while (paramString2 != null);
    paramString1 = paramBundle.getString("error");
    if ("RST".equals(paramString1))
    {
      zzsk();
      throw new IOException("INSTANCE_ID_RESET");
    }
    if (paramString1 != null) {
      throw new IOException(paramString1);
    }
    paramString1 = String.valueOf(paramBundle);
    Log.w("FirebaseInstanceId", String.valueOf(paramString1).length() + 21 + "Unexpected response: " + paramString1, new Throwable());
    throw new IOException("SERVICE_NOT_AVAILABLE");
  }
  
  private final void zzse()
  {
    zzab localzzab = zzsg();
    if ((localzzab == null) || (localzzab.zzff(this.zzbqn.zzsv())) || (zzbqk.zztc() != null)) {
      startSync();
    }
  }
  
  static zzaa zzsi()
  {
    return zzbqk;
  }
  
  static boolean zzsj()
  {
    if ((Log.isLoggable("FirebaseInstanceId", 3)) || ((Build.VERSION.SDK_INT == 23) && (Log.isLoggable("FirebaseInstanceId", 3)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private final boolean zzsm()
  {
    Object localObject1 = this.zzbqm.getApplicationContext();
    Object localObject2 = ((Context)localObject1).getSharedPreferences("com.google.firebase.messaging", 0);
    boolean bool;
    if (((SharedPreferences)localObject2).contains("auto_init")) {
      bool = ((SharedPreferences)localObject2).getBoolean("auto_init", true);
    }
    for (;;)
    {
      return bool;
      try
      {
        localObject2 = ((Context)localObject1).getPackageManager();
        if (localObject2 != null)
        {
          localObject1 = ((PackageManager)localObject2).getApplicationInfo(((Context)localObject1).getPackageName(), 128);
          if ((localObject1 != null) && (((ApplicationInfo)localObject1).metaData != null) && (((ApplicationInfo)localObject1).metaData.containsKey("firebase_messaging_auto_init_enabled"))) {
            bool = ((ApplicationInfo)localObject1).metaData.getBoolean("firebase_messaging_auto_init_enabled");
          }
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        bool = zzsn();
      }
    }
  }
  
  private final boolean zzsn()
  {
    boolean bool = true;
    try
    {
      Class.forName("com.google.firebase.messaging.FirebaseMessaging");
      return bool;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;)
      {
        Object localObject = this.zzbqm.getApplicationContext();
        Intent localIntent = new Intent("com.google.firebase.MESSAGING_EVENT");
        localIntent.setPackage(((Context)localObject).getPackageName());
        localObject = ((Context)localObject).getPackageManager().resolveService(localIntent, 0);
        if ((localObject == null) || (((ResolveInfo)localObject).serviceInfo == null)) {
          bool = false;
        }
      }
    }
  }
  
  public String getId()
  {
    zzse();
    return zzw.zza(getKeyPair());
  }
  
  public String getToken()
  {
    Object localObject = zzsg();
    if ((localObject == null) || (((zzab)localObject).zzff(this.zzbqn.zzsv()))) {
      startSync();
    }
    if (localObject != null) {}
    for (localObject = ((zzab)localObject).zzbsb;; localObject = null) {
      return (String)localObject;
    }
  }
  
  public String getToken(String paramString1, String paramString2)
    throws IOException
  {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    Object localObject = zzbqk.zzj("", paramString1, paramString2);
    if ((localObject != null) && (!((zzab)localObject).zzff(this.zzbqn.zzsv()))) {
      localObject = ((zzab)localObject).zzbsb;
    }
    for (;;)
    {
      return (String)localObject;
      String str = zzb(paramString1, paramString2, new Bundle());
      localObject = str;
      if (str != null)
      {
        zzbqk.zza("", paramString1, paramString2, str, this.zzbqn.zzsv());
        localObject = str;
      }
    }
  }
  
  final void zzan(long paramLong)
  {
    try
    {
      long l = Math.min(Math.max(30L, paramLong << 1), zzbqi);
      zzac localzzac = new com/google/firebase/iid/zzac;
      localzzac.<init>(this, this.zzbqn, l);
      zza(localzzac, paramLong);
      this.zzbqq = true;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final void zzew(String paramString)
    throws IOException
  {
    Object localObject = zzsg();
    if ((localObject == null) || (((zzab)localObject).zzff(this.zzbqn.zzsv()))) {
      throw new IOException("token not available");
    }
    Bundle localBundle = new Bundle();
    String str1 = String.valueOf("/topics/");
    String str2 = String.valueOf(paramString);
    if (str2.length() != 0)
    {
      str2 = str1.concat(str2);
      localBundle.putString("gcm.topic", str2);
      str2 = ((zzab)localObject).zzbsb;
      localObject = String.valueOf("/topics/");
      paramString = String.valueOf(paramString);
      if (paramString.length() == 0) {
        break label137;
      }
    }
    label137:
    for (paramString = ((String)localObject).concat(paramString);; paramString = new String((String)localObject))
    {
      zzb(str2, paramString, localBundle);
      return;
      str2 = new String(str1);
      break;
    }
  }
  
  final void zzex(String paramString)
    throws IOException
  {
    Object localObject = zzsg();
    if ((localObject == null) || (((zzab)localObject).zzff(this.zzbqn.zzsv()))) {
      throw new IOException("token not available");
    }
    Bundle localBundle = new Bundle();
    String str1 = String.valueOf("/topics/");
    String str2 = String.valueOf(paramString);
    if (str2.length() != 0)
    {
      str1 = str1.concat(str2);
      localBundle.putString("gcm.topic", str1);
      localBundle.putString("delete", "1");
      str1 = ((zzab)localObject).zzbsb;
      localObject = String.valueOf("/topics/");
      paramString = String.valueOf(paramString);
      if (paramString.length() == 0) {
        break label147;
      }
    }
    label147:
    for (paramString = ((String)localObject).concat(paramString);; paramString = new String((String)localObject))
    {
      zzb(str1, paramString, localBundle);
      return;
      str1 = new String(str1);
      break;
    }
  }
  
  final FirebaseApp zzsf()
  {
    return this.zzbqm;
  }
  
  final zzab zzsg()
  {
    return zzbqk.zzj("", zzw.zza(this.zzbqm), "*");
  }
  
  final String zzsh()
    throws IOException
  {
    return getToken(zzw.zza(this.zzbqm), "*");
  }
  
  final void zzsk()
  {
    try
    {
      zzbqk.zztd();
      this.zzbqp = null;
      if (zzso()) {
        startSync();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final void zzsl()
  {
    zzbqk.zzfc("");
    startSync();
  }
  
  public final boolean zzso()
  {
    try
    {
      boolean bool = this.zzbqr;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final void zzu(boolean paramBoolean)
  {
    try
    {
      this.zzbqq = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/FirebaseInstanceId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */