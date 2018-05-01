package com.google.android.gms.gcm;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.R.string;
import com.google.android.gms.common.util.zzq;
import java.util.Iterator;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;

final class zza
{
  static zza zzibw;
  private final Context mContext;
  private String zzibx;
  private final AtomicInteger zziby = new AtomicInteger((int)SystemClock.elapsedRealtime());
  
  private zza(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  private final Bundle zzauu()
  {
    Object localObject = null;
    try
    {
      ApplicationInfo localApplicationInfo = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 128);
      localObject = localApplicationInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
    }
    if ((localObject != null) && (((ApplicationInfo)localObject).metaData != null)) {
      return ((ApplicationInfo)localObject).metaData;
    }
    return Bundle.EMPTY;
  }
  
  static zza zzdj(Context paramContext)
  {
    try
    {
      if (zzibw == null) {
        zzibw = new zza(paramContext);
      }
      paramContext = zzibw;
      return paramContext;
    }
    finally {}
  }
  
  static boolean zzdk(Context paramContext)
  {
    if (((KeyguardManager)paramContext.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
      return false;
    }
    int i = Process.myPid();
    paramContext = ((ActivityManager)paramContext.getSystemService("activity")).getRunningAppProcesses();
    if (paramContext != null)
    {
      paramContext = paramContext.iterator();
      while (paramContext.hasNext())
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)paramContext.next();
        if (localRunningAppProcessInfo.pid == i) {
          return localRunningAppProcessInfo.importance == 100;
        }
      }
    }
    return false;
  }
  
  static String zze(Bundle paramBundle, String paramString)
  {
    String str2 = paramBundle.getString(paramString);
    String str1 = str2;
    if (str2 == null) {
      str1 = paramBundle.getString(paramString.replace("gcm.n.", "gcm.notification."));
    }
    return str1;
  }
  
  private final String zzf(Bundle paramBundle, String paramString)
  {
    String str1 = zze(paramBundle, paramString);
    if (!TextUtils.isEmpty(str1)) {
      return str1;
    }
    str1 = String.valueOf(paramString);
    String str2 = String.valueOf("_loc_key");
    if (str2.length() != 0) {}
    for (str1 = str1.concat(str2);; str1 = new String(str1))
    {
      str2 = zze(paramBundle, str1);
      if (!TextUtils.isEmpty(str2)) {
        break;
      }
      return null;
    }
    Resources localResources = this.mContext.getResources();
    int j = localResources.getIdentifier(str2, "string", this.mContext.getPackageName());
    if (j == 0)
    {
      paramBundle = String.valueOf(paramString);
      paramString = String.valueOf("_loc_key");
      if (paramString.length() != 0) {}
      for (paramBundle = paramBundle.concat(paramString);; paramBundle = new String(paramBundle))
      {
        paramBundle = paramBundle.substring(6);
        Log.w("GcmNotification", String.valueOf(paramBundle).length() + 49 + String.valueOf(str2).length() + paramBundle + " resource not found: " + str2 + " Default value will be used.");
        return null;
      }
    }
    str1 = String.valueOf(paramString);
    Object localObject = String.valueOf("_loc_args");
    if (((String)localObject).length() != 0) {}
    for (str1 = str1.concat((String)localObject);; str1 = new String(str1))
    {
      str1 = zze(paramBundle, str1);
      if (!TextUtils.isEmpty(str1)) {
        break;
      }
      return localResources.getString(j);
    }
    try
    {
      paramBundle = new JSONArray(str1);
      localObject = new String[paramBundle.length()];
      int i = 0;
      while (i < localObject.length)
      {
        localObject[i] = paramBundle.opt(i);
        i += 1;
      }
      paramBundle = localResources.getString(j, (Object[])localObject);
      return paramBundle;
    }
    catch (JSONException paramBundle)
    {
      paramBundle = String.valueOf(paramString);
      paramString = String.valueOf("_loc_args");
      if (paramString.length() != 0) {}
      for (paramBundle = paramBundle.concat(paramString);; paramBundle = new String(paramBundle))
      {
        paramBundle = paramBundle.substring(6);
        Log.w("GcmNotification", String.valueOf(paramBundle).length() + 41 + String.valueOf(str1).length() + "Malformed " + paramBundle + ": " + str1 + "  Default value will be used.");
        return null;
      }
    }
    catch (MissingFormatArgumentException paramBundle)
    {
      for (;;)
      {
        Log.w("GcmNotification", String.valueOf(str2).length() + 58 + String.valueOf(str1).length() + "Missing format argument for " + str2 + ": " + str1 + " Default value will be used.", paramBundle);
      }
    }
  }
  
  static void zzr(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      String str3 = paramBundle.getString(str2);
      str1 = str2;
      if (str2.startsWith("gcm.notification.")) {
        str1 = str2.replace("gcm.notification.", "gcm.n.");
      }
      if (str1.startsWith("gcm.n."))
      {
        if (!"gcm.n.e".equals(str1)) {
          localBundle.putString(str1.substring(6), str3);
        }
        localIterator.remove();
      }
    }
    String str1 = localBundle.getString("sound2");
    if (str1 != null)
    {
      localBundle.remove("sound2");
      localBundle.putString("sound", str1);
    }
    if (!localBundle.isEmpty()) {
      paramBundle.putBundle("notification", localBundle);
    }
  }
  
  private final PendingIntent zzt(Bundle paramBundle)
  {
    Object localObject = zze(paramBundle, "gcm.n.click_action");
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = new Intent((String)localObject);
      ((Intent)localObject).setPackage(this.mContext.getPackageName());
      ((Intent)localObject).setFlags(268435456);
    }
    label170:
    for (;;)
    {
      paramBundle = new Bundle(paramBundle);
      GcmListenerService.zzq(paramBundle);
      ((Intent)localObject).putExtras(paramBundle);
      paramBundle = paramBundle.keySet().iterator();
      while (paramBundle.hasNext())
      {
        String str = (String)paramBundle.next();
        if ((str.startsWith("gcm.n.")) || (str.startsWith("gcm.notification.")))
        {
          ((Intent)localObject).removeExtra(str);
          continue;
          localObject = this.mContext.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName());
          if (localObject != null) {
            break label170;
          }
          Log.w("GcmNotification", "No activity found to launch app");
          return null;
        }
      }
      return PendingIntent.getActivity(this.mContext, this.zziby.getAndIncrement(), (Intent)localObject, NUM);
    }
  }
  
  final boolean zzs(Bundle paramBundle)
  {
    Object localObject1 = null;
    Object localObject3 = zzf(paramBundle, "gcm.n.title");
    if (TextUtils.isEmpty((CharSequence)localObject3)) {
      localObject3 = this.mContext.getApplicationInfo().loadLabel(this.mContext.getPackageManager());
    }
    for (;;)
    {
      String str1 = zzf(paramBundle, "gcm.n.body");
      Object localObject2 = zze(paramBundle, "gcm.n.icon");
      Object localObject4;
      int i;
      String str2;
      label127:
      PendingIntent localPendingIntent;
      if (!TextUtils.isEmpty((CharSequence)localObject2))
      {
        localObject4 = this.mContext.getResources();
        i = ((Resources)localObject4).getIdentifier((String)localObject2, "drawable", this.mContext.getPackageName());
        if (i != 0)
        {
          str2 = zze(paramBundle, "gcm.n.color");
          localObject2 = zze(paramBundle, "gcm.n.sound2");
          if (!TextUtils.isEmpty((CharSequence)localObject2)) {
            break label492;
          }
          localObject2 = null;
          localPendingIntent = zzt(paramBundle);
          if ((!zzq.isAtLeastO()) || (this.mContext.getApplicationInfo().targetSdkVersion <= 25)) {
            break label834;
          }
          localObject4 = zze(paramBundle, "gcm.n.android_channel_id");
          if (zzq.isAtLeastO()) {
            break label615;
          }
          label170:
          localObject4 = new Notification.Builder(this.mContext).setAutoCancel(true).setSmallIcon(i);
          if (!TextUtils.isEmpty((CharSequence)localObject3)) {
            ((Notification.Builder)localObject4).setContentTitle((CharSequence)localObject3);
          }
          if (!TextUtils.isEmpty(str1))
          {
            ((Notification.Builder)localObject4).setContentText(str1);
            ((Notification.Builder)localObject4).setStyle(new Notification.BigTextStyle().bigText(str1));
          }
          if (!TextUtils.isEmpty(str2)) {
            ((Notification.Builder)localObject4).setColor(Color.parseColor(str2));
          }
          if (localObject2 != null) {
            ((Notification.Builder)localObject4).setSound((Uri)localObject2);
          }
          if (localPendingIntent != null) {
            ((Notification.Builder)localObject4).setContentIntent(localPendingIntent);
          }
          if (localObject1 != null) {
            ((Notification.Builder)localObject4).setChannelId((String)localObject1);
          }
        }
      }
      for (localObject1 = ((Notification.Builder)localObject4).build();; localObject1 = ((NotificationCompat.Builder)localObject1).build())
      {
        localObject2 = zze(paramBundle, "gcm.n.tag");
        if (Log.isLoggable("GcmNotification", 3)) {
          Log.d("GcmNotification", "Showing notification");
        }
        localObject3 = (NotificationManager)this.mContext.getSystemService("notification");
        paramBundle = (Bundle)localObject2;
        if (TextUtils.isEmpty((CharSequence)localObject2))
        {
          long l = SystemClock.uptimeMillis();
          paramBundle = 37 + "GCM-Notification:" + l;
        }
        ((NotificationManager)localObject3).notify(paramBundle, 0, (Notification)localObject1);
        return true;
        i = ((Resources)localObject4).getIdentifier((String)localObject2, "mipmap", this.mContext.getPackageName());
        if (i != 0) {
          break;
        }
        Log.w("GcmNotification", String.valueOf(localObject2).length() + 57 + "Icon resource " + (String)localObject2 + " not found. Notification will use app icon.");
        int j = this.mContext.getApplicationInfo().icon;
        i = j;
        if (j == 0) {
          i = 17301651;
        }
        break;
        label492:
        if ((!"default".equals(localObject2)) && (this.mContext.getResources().getIdentifier((String)localObject2, "raw", this.mContext.getPackageName()) != 0))
        {
          localObject4 = this.mContext.getPackageName();
          localObject2 = Uri.parse(String.valueOf("android.resource://").length() + 5 + String.valueOf(localObject4).length() + String.valueOf(localObject2).length() + "android.resource://" + (String)localObject4 + "/raw/" + (String)localObject2);
          break label127;
        }
        localObject2 = RingtoneManager.getDefaultUri(2);
        break label127;
        label615:
        localObject1 = (NotificationManager)this.mContext.getSystemService(NotificationManager.class);
        if (!TextUtils.isEmpty((CharSequence)localObject4))
        {
          if (((NotificationManager)localObject1).getNotificationChannel((String)localObject4) != null)
          {
            localObject1 = localObject4;
            break label170;
          }
          Log.w("GcmNotification", String.valueOf(localObject4).length() + 122 + "Notification Channel requested (" + (String)localObject4 + ") has not been created by the app. Manifest configuration, or default, value will be used.");
        }
        if (this.zzibx != null)
        {
          localObject1 = this.zzibx;
          break label170;
        }
        this.zzibx = zzauu().getString("com.google.android.gms.gcm.default_notification_channel_id");
        if (!TextUtils.isEmpty(this.zzibx))
        {
          if (((NotificationManager)localObject1).getNotificationChannel(this.zzibx) != null)
          {
            localObject1 = this.zzibx;
            break label170;
          }
          Log.w("GcmNotification", "Notification Channel set in AndroidManifest.xml has not been created by the app. Default value will be used.");
        }
        for (;;)
        {
          if (((NotificationManager)localObject1).getNotificationChannel("fcm_fallback_notification_channel") == null) {
            ((NotificationManager)localObject1).createNotificationChannel(new NotificationChannel("fcm_fallback_notification_channel", this.mContext.getString(R.string.gcm_fallback_notification_channel_label), 3));
          }
          this.zzibx = "fcm_fallback_notification_channel";
          localObject1 = this.zzibx;
          break;
          Log.w("GcmNotification", "Missing Default Notification Channel metadata in AndroidManifest. Default value will be used.");
        }
        label834:
        localObject1 = new NotificationCompat.Builder(this.mContext).setAutoCancel(true).setSmallIcon(i);
        if (!TextUtils.isEmpty((CharSequence)localObject3)) {
          ((NotificationCompat.Builder)localObject1).setContentTitle((CharSequence)localObject3);
        }
        if (!TextUtils.isEmpty(str1)) {
          ((NotificationCompat.Builder)localObject1).setContentText(str1);
        }
        if (!TextUtils.isEmpty(str2)) {
          ((NotificationCompat.Builder)localObject1).setColor(Color.parseColor(str2));
        }
        if (localObject2 != null) {
          ((NotificationCompat.Builder)localObject1).setSound((Uri)localObject2);
        }
        if (localPendingIntent != null) {
          ((NotificationCompat.Builder)localObject1).setContentIntent(localPendingIntent);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */