package com.google.firebase.iid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import java.io.IOException;

public class FirebaseInstanceIdService
  extends zzb
{
  private static BroadcastReceiver bhu;
  private static final Object bhv = new Object();
  private static boolean bhw = false;
  private boolean bhx = false;
  
  static void zza(Context paramContext, FirebaseInstanceId paramFirebaseInstanceId)
  {
    synchronized (bhv)
    {
      if (bhw) {
        return;
      }
      ??? = paramFirebaseInstanceId.C();
      if ((??? == null) || (((zzg.zza)???).zztz(zzd.afY)) || (paramFirebaseInstanceId.E().J() != null))
      {
        zzet(paramContext);
        return;
      }
    }
  }
  
  private void zza(Intent paramIntent, boolean paramBoolean)
  {
    synchronized (bhv)
    {
      bhw = false;
      if (zzf.zzdj(this) == null) {
        return;
      }
    }
    ??? = FirebaseInstanceId.getInstance();
    if (((FirebaseInstanceId)???).C() == null) {
      try
      {
        if (((FirebaseInstanceId)???).D() != null)
        {
          if (this.bhx) {
            Log.d("FirebaseInstanceId", "get master token succeeded");
          }
          zza(this, (FirebaseInstanceId)???);
          onTokenRefresh();
          return;
        }
      }
      catch (IOException localIOException1)
      {
        zzd(paramIntent, localIOException1.getMessage());
        return;
        zzd(paramIntent, "returned token is null");
        return;
      }
      catch (SecurityException paramIntent)
      {
        Log.e("FirebaseInstanceId", "Unable to get master token", paramIntent);
        return;
      }
    }
    zze localzze = localIOException1.E();
    String str = localzze.J();
    if (str != null)
    {
      Object localObject3 = str.split("!");
      Object localObject2;
      int j;
      if (localObject3.length == 2)
      {
        localObject2 = localObject3[0];
        localObject3 = localObject3[1];
        j = -1;
      }
      for (;;)
      {
        try
        {
          int k = ((String)localObject2).hashCode();
          int i = j;
          switch (k)
          {
          default: 
            i = j;
          case 84: 
            switch (i)
            {
            default: 
              localzze.zztv(str);
              str = localzze.J();
            }
            break;
          case 83: 
            i = j;
            if (!((String)localObject2).equals("S")) {
              continue;
            }
            i = 0;
            break;
          case 85: 
            i = j;
            if (!((String)localObject2).equals("U")) {
              continue;
            }
            i = 1;
            continue;
            FirebaseInstanceId.getInstance().zzts((String)localObject3);
            if (!this.bhx) {
              continue;
            }
            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
            continue;
            FirebaseInstanceId.getInstance().zztt((String)localObject3);
          }
        }
        catch (IOException localIOException2)
        {
          zzd(paramIntent, localIOException2.getMessage());
          return;
        }
        if (this.bhx) {
          Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
        }
      }
    }
    Log.d("FirebaseInstanceId", "topic sync succeeded");
  }
  
  private void zza(zzf paramzzf, Bundle paramBundle)
  {
    String str = zzf.zzdj(this);
    if (str == null)
    {
      Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
      return;
    }
    Intent localIntent = new Intent("com.google.android.gcm.intent.SEND");
    localIntent.setPackage(str);
    localIntent.putExtras(paramBundle);
    paramzzf.zzs(localIntent);
    localIntent.putExtra("google.to", "google.com/iid");
    localIntent.putExtra("google.message_id", zzf.zzbov());
    sendOrderedBroadcast(localIntent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
  }
  
  private static Intent zzagk(int paramInt)
  {
    Context localContext = FirebaseApp.getInstance().getApplicationContext();
    Intent localIntent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
    localIntent.putExtra("next_retry_delay_in_seconds", paramInt);
    return FirebaseInstanceIdInternalReceiver.zzg(localContext, localIntent);
  }
  
  private void zzagl(int paramInt)
  {
    AlarmManager localAlarmManager = (AlarmManager)getSystemService("alarm");
    PendingIntent localPendingIntent = PendingIntent.getBroadcast(this, 0, zzagk(paramInt * 2), 268435456);
    localAlarmManager.set(3, SystemClock.elapsedRealtime() + paramInt * 1000, localPendingIntent);
  }
  
  private String zzai(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("subtype");
    paramIntent = str;
    if (str == null) {
      paramIntent = "";
    }
    return paramIntent;
  }
  
  private int zzb(Intent paramIntent, boolean paramBoolean)
  {
    int j = 10;
    int i;
    if (paramIntent == null)
    {
      i = 10;
      if ((i >= 10) || (paramBoolean)) {
        break label39;
      }
      j = 30;
    }
    label39:
    while (i < 10)
    {
      return j;
      i = paramIntent.getIntExtra("next_retry_delay_in_seconds", 0);
      break;
    }
    if (i > 28800) {
      return 28800;
    }
    return i;
  }
  
  private void zzd(Intent arg1, String paramString)
  {
    boolean bool = zzeu(this);
    final int i = zzb(???, bool);
    Log.d("FirebaseInstanceId", String.valueOf(paramString).length() + 47 + "background sync failed: " + paramString + ", retry in " + i + "s");
    synchronized (bhv)
    {
      zzagl(i);
      bhw = true;
      if (!bool)
      {
        if (this.bhx) {
          Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
        }
        if (bhu == null) {
          bhu = new BroadcastReceiver()
          {
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
            {
              if (FirebaseInstanceIdService.zzev(paramAnonymousContext))
              {
                if (FirebaseInstanceIdService.zza(FirebaseInstanceIdService.this)) {
                  Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                }
                FirebaseInstanceIdService.this.getApplicationContext().unregisterReceiver(this);
                paramAnonymousContext.sendBroadcast(FirebaseInstanceIdService.zzagm(i));
              }
            }
          };
        }
        getApplicationContext().registerReceiver(bhu, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
      }
      return;
    }
  }
  
  static void zzet(Context paramContext)
  {
    if (zzf.zzdj(paramContext) == null) {
      return;
    }
    synchronized (bhv)
    {
      if (!bhw)
      {
        paramContext.sendBroadcast(zzagk(0));
        bhw = true;
      }
      return;
    }
  }
  
  private static boolean zzeu(Context paramContext)
  {
    paramContext = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    return (paramContext != null) && (paramContext.isConnected());
  }
  
  private zzd zztu(String paramString)
  {
    if (paramString == null) {
      return zzd.zzb(this, null);
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("subtype", paramString);
    return zzd.zzb(this, localBundle);
  }
  
  @WorkerThread
  public void onTokenRefresh() {}
  
  protected Intent zzae(Intent paramIntent)
  {
    return FirebaseInstanceIdInternalReceiver.F();
  }
  
  public boolean zzag(Intent paramIntent)
  {
    this.bhx = Log.isLoggable("FirebaseInstanceId", 3);
    if ((paramIntent.getStringExtra("error") != null) || (paramIntent.getStringExtra("registration_id") != null))
    {
      String str2 = zzai(paramIntent);
      if (this.bhx)
      {
        str1 = String.valueOf(str2);
        if (str1.length() == 0) {
          break label84;
        }
      }
      label84:
      for (String str1 = "Register result in service ".concat(str1);; str1 = new String("Register result in service "))
      {
        Log.d("FirebaseInstanceId", str1);
        zztu(str2).I().zzv(paramIntent);
        return true;
      }
    }
    return false;
  }
  
  public void zzah(Intent paramIntent)
  {
    String str2 = zzai(paramIntent);
    zzd localzzd = zztu(str2);
    String str1 = paramIntent.getStringExtra("CMD");
    Object localObject;
    if (this.bhx)
    {
      localObject = String.valueOf(paramIntent.getExtras());
      Log.d("FirebaseInstanceId", String.valueOf(str2).length() + 18 + String.valueOf(str1).length() + String.valueOf(localObject).length() + "Service command " + str2 + " " + str1 + " " + (String)localObject);
    }
    if (paramIntent.getStringExtra("unregistered") != null)
    {
      localObject = localzzd.H();
      str1 = str2;
      if (str2 == null) {
        str1 = "";
      }
      ((zzg)localObject).zzku(str1);
      localzzd.I().zzv(paramIntent);
    }
    do
    {
      do
      {
        return;
        if ("gcm.googleapis.com/refresh".equals(paramIntent.getStringExtra("from")))
        {
          localzzd.H().zzku(str2);
          zza(paramIntent, false);
          return;
        }
        if ("RST".equals(str1))
        {
          localzzd.zzboq();
          zza(paramIntent, true);
          return;
        }
        if (!"RST_FULL".equals(str1)) {
          break;
        }
      } while (localzzd.H().isEmpty());
      localzzd.zzboq();
      localzzd.H().zzbow();
      zza(paramIntent, true);
      return;
      if ("SYNC".equals(str1))
      {
        localzzd.H().zzku(str2);
        zza(paramIntent, false);
        return;
      }
    } while (!"PING".equals(str1));
    zza(localzzd.I(), paramIntent.getExtras());
  }
  
  public void zzm(Intent paramIntent)
  {
    String str2 = paramIntent.getAction();
    String str1 = str2;
    if (str2 == null) {
      str1 = "";
    }
    switch (str1.hashCode())
    {
    }
    label40:
    for (int i = -1;; i = 0) {
      switch (i)
      {
      default: 
        zzah(paramIntent);
        return;
        if (!str1.equals("ACTION_TOKEN_REFRESH_RETRY")) {
          break label40;
        }
      }
    }
    zza(paramIntent, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/FirebaseInstanceIdService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */