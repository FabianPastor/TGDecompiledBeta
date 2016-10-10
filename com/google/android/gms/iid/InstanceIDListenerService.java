package com.google.android.gms.iid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class InstanceIDListenerService
  extends Service
{
  static String ACTION = "action";
  private static String aeK = "gcm.googleapis.com/refresh";
  private static String agb = "google.com/iid";
  private static String agc = "CMD";
  MessengerCompat afZ = new MessengerCompat(new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      InstanceIDListenerService.zza(InstanceIDListenerService.this, paramAnonymousMessage, MessengerCompat.zzc(paramAnonymousMessage));
    }
  });
  BroadcastReceiver aga = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (Log.isLoggable("InstanceID", 3))
      {
        paramAnonymousIntent.getStringExtra("registration_id");
        paramAnonymousContext = String.valueOf(paramAnonymousIntent.getExtras());
        Log.d("InstanceID", String.valueOf(paramAnonymousContext).length() + 46 + "Received GSF callback using dynamic receiver: " + paramAnonymousContext);
      }
      InstanceIDListenerService.this.zzn(paramAnonymousIntent);
      InstanceIDListenerService.this.stop();
    }
  };
  int agd;
  int age;
  
  static void zza(Context paramContext, zzd paramzzd)
  {
    paramzzd.zzbow();
    paramzzd = new Intent("com.google.android.gms.iid.InstanceID");
    paramzzd.putExtra(agc, "RST");
    paramzzd.setPackage(paramContext.getPackageName());
    paramContext.startService(paramzzd);
  }
  
  private void zza(Message paramMessage, int paramInt)
  {
    zzc.zzdj(this);
    getPackageManager();
    if ((paramInt != zzc.agl) && (paramInt != zzc.agk))
    {
      int i = zzc.agk;
      int j = zzc.agl;
      Log.w("InstanceID", 77 + "Message from unexpected caller " + paramInt + " mine=" + i + " appid=" + j);
      return;
    }
    zzn((Intent)paramMessage.obj);
  }
  
  static void zzdi(Context paramContext)
  {
    Intent localIntent = new Intent("com.google.android.gms.iid.InstanceID");
    localIntent.setPackage(paramContext.getPackageName());
    localIntent.putExtra(agc, "SYNC");
    paramContext.startService(localIntent);
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if ((paramIntent != null) && ("com.google.android.gms.iid.InstanceID".equals(paramIntent.getAction()))) {
      return this.afZ.getBinder();
    }
    return null;
  }
  
  public void onCreate()
  {
    IntentFilter localIntentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
    localIntentFilter.addCategory(getPackageName());
    registerReceiver(this.aga, localIntentFilter, "com.google.android.c2dm.permission.RECEIVE", null);
  }
  
  public void onDestroy()
  {
    unregisterReceiver(this.aga);
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    zztt(paramInt2);
    if (paramIntent == null)
    {
      stop();
      return 2;
    }
    try
    {
      if ("com.google.android.gms.iid.InstanceID".equals(paramIntent.getAction()))
      {
        if (Build.VERSION.SDK_INT <= 18)
        {
          Intent localIntent = (Intent)paramIntent.getParcelableExtra("GSF");
          if (localIntent != null)
          {
            startService(localIntent);
            return 1;
          }
        }
        zzn(paramIntent);
      }
      stop();
      if (paramIntent.getStringExtra("from") != null) {
        WakefulBroadcastReceiver.completeWakefulIntent(paramIntent);
      }
      return 2;
    }
    finally
    {
      stop();
    }
  }
  
  public void onTokenRefresh() {}
  
  void stop()
  {
    try
    {
      this.agd -= 1;
      if (this.agd == 0) {
        stopSelf(this.age);
      }
      if (Log.isLoggable("InstanceID", 3))
      {
        int i = this.agd;
        int j = this.age;
        Log.d("InstanceID", 28 + "Stop " + i + " " + j);
      }
      return;
    }
    finally {}
  }
  
  public void zzcb(boolean paramBoolean)
  {
    onTokenRefresh();
  }
  
  public void zzn(Intent paramIntent)
  {
    String str2 = paramIntent.getStringExtra("subtype");
    Object localObject1;
    String str1;
    if (str2 == null)
    {
      localObject1 = InstanceID.getInstance(this);
      str1 = paramIntent.getStringExtra(agc);
      if ((paramIntent.getStringExtra("error") == null) && (paramIntent.getStringExtra("registration_id") == null)) {
        break label131;
      }
      if (Log.isLoggable("InstanceID", 3))
      {
        str1 = String.valueOf(str2);
        if (str1.length() == 0) {
          break label117;
        }
        str1 = "Register result in service ".concat(str1);
        label76:
        Log.d("InstanceID", str1);
      }
      ((InstanceID)localObject1).zzbos().zzv(paramIntent);
    }
    label117:
    label131:
    label348:
    do
    {
      do
      {
        return;
        localObject1 = new Bundle();
        ((Bundle)localObject1).putString("subtype", str2);
        localObject1 = InstanceID.zza(this, (Bundle)localObject1);
        break;
        str1 = new String("Register result in service ");
        break label76;
        Object localObject2;
        if (Log.isLoggable("InstanceID", 3))
        {
          localObject2 = String.valueOf(paramIntent.getExtras());
          Log.d("InstanceID", String.valueOf(str2).length() + 18 + String.valueOf(str1).length() + String.valueOf(localObject2).length() + "Service command " + str2 + " " + str1 + " " + (String)localObject2);
        }
        if (paramIntent.getStringExtra("unregistered") != null)
        {
          localObject2 = ((InstanceID)localObject1).zzbor();
          str1 = str2;
          if (str2 == null) {
            str1 = "";
          }
          ((zzd)localObject2).zzku(str1);
          ((InstanceID)localObject1).zzbos().zzv(paramIntent);
          return;
        }
        if (aeK.equals(paramIntent.getStringExtra("from")))
        {
          ((InstanceID)localObject1).zzbor().zzku(str2);
          zzcb(false);
          return;
        }
        if ("RST".equals(str1))
        {
          ((InstanceID)localObject1).zzboq();
          zzcb(true);
          return;
        }
        if (!"RST_FULL".equals(str1)) {
          break label348;
        }
      } while (((InstanceID)localObject1).zzbor().isEmpty());
      ((InstanceID)localObject1).zzbor().zzbow();
      zzcb(true);
      return;
      if ("SYNC".equals(str1))
      {
        ((InstanceID)localObject1).zzbor().zzku(str2);
        zzcb(false);
        return;
      }
    } while (!"PING".equals(str1));
  }
  
  void zztt(int paramInt)
  {
    try
    {
      this.agd += 1;
      if (paramInt > this.age) {
        this.age = paramInt;
      }
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/InstanceIDListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */