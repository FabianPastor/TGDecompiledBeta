package com.google.firebase.messaging;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.firebase.iid.zzz;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;

final class zza
{
  private static zza zzbsg;
  private Bundle zzbsh;
  private Method zzbsi;
  private Method zzbsj;
  private final AtomicInteger zzbsk = new AtomicInteger((int)SystemClock.elapsedRealtime());
  private final Context zzqs;
  
  private zza(Context paramContext)
  {
    this.zzqs = paramContext.getApplicationContext();
  }
  
  @TargetApi(26)
  private final Notification zza(CharSequence paramCharSequence, String paramString1, int paramInt, Integer paramInteger, Uri paramUri, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String paramString2)
  {
    Notification.Builder localBuilder = new Notification.Builder(this.zzqs).setAutoCancel(true).setSmallIcon(paramInt);
    if (!TextUtils.isEmpty(paramCharSequence)) {
      localBuilder.setContentTitle(paramCharSequence);
    }
    if (!TextUtils.isEmpty(paramString1))
    {
      localBuilder.setContentText(paramString1);
      localBuilder.setStyle(new Notification.BigTextStyle().bigText(paramString1));
    }
    if (paramInteger != null) {
      localBuilder.setColor(paramInteger.intValue());
    }
    if (paramUri != null) {
      localBuilder.setSound(paramUri);
    }
    if (paramPendingIntent1 != null) {
      localBuilder.setContentIntent(paramPendingIntent1);
    }
    if (paramPendingIntent2 != null) {
      localBuilder.setDeleteIntent(paramPendingIntent2);
    }
    if (paramString2 != null)
    {
      if (this.zzbsi == null) {
        this.zzbsi = zzfh("setChannelId");
      }
      if (this.zzbsi == null) {
        this.zzbsi = zzfh("setChannel");
      }
      if (this.zzbsi != null) {
        break label179;
      }
      Log.e("FirebaseMessaging", "Error while setting the notification channel");
    }
    for (;;)
    {
      return localBuilder.build();
      try
      {
        label179:
        this.zzbsi.invoke(localBuilder, new Object[] { paramString2 });
      }
      catch (IllegalAccessException paramCharSequence)
      {
        Log.e("FirebaseMessaging", "Error while setting the notification channel", paramCharSequence);
      }
      catch (InvocationTargetException paramCharSequence)
      {
        Log.e("FirebaseMessaging", "Error while setting the notification channel", paramCharSequence);
      }
      catch (SecurityException paramCharSequence)
      {
        Log.e("FirebaseMessaging", "Error while setting the notification channel", paramCharSequence);
      }
      catch (IllegalArgumentException paramCharSequence)
      {
        Log.e("FirebaseMessaging", "Error while setting the notification channel", paramCharSequence);
      }
    }
  }
  
  static String zza(Bundle paramBundle, String paramString)
  {
    String str1 = paramBundle.getString(paramString);
    String str2 = str1;
    if (str1 == null) {
      str2 = paramBundle.getString(paramString.replace("gcm.n.", "gcm.notification."));
    }
    return str2;
  }
  
  private static void zza(Intent paramIntent, Bundle paramBundle)
  {
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((str.startsWith("google.c.a.")) || (str.equals("from"))) {
        paramIntent.putExtra(str, paramBundle.getString(str));
      }
    }
  }
  
  @TargetApi(26)
  private final boolean zzaf(int paramInt)
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT != 26) {}
    for (;;)
    {
      return bool;
      try
      {
        if ((this.zzqs.getResources().getDrawable(paramInt, null) instanceof AdaptiveIconDrawable))
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>(77);
          Log.e("FirebaseMessaging", "Adaptive icons cannot be used in notifications. Ignoring icon id: " + paramInt);
          bool = false;
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        bool = false;
      }
    }
  }
  
  static String zzb(Bundle paramBundle, String paramString)
  {
    paramString = String.valueOf(paramString);
    String str = String.valueOf("_loc_key");
    if (str.length() != 0) {}
    for (paramString = paramString.concat(str);; paramString = new String(paramString)) {
      return zza(paramBundle, paramString);
    }
  }
  
  static Object[] zzc(Bundle paramBundle, String paramString)
  {
    Object localObject = String.valueOf(paramString);
    String str = String.valueOf("_loc_args");
    if (str.length() != 0)
    {
      localObject = ((String)localObject).concat(str);
      str = zza(paramBundle, (String)localObject);
      if (!TextUtils.isEmpty(str)) {
        break label54;
      }
      paramBundle = null;
    }
    for (;;)
    {
      return paramBundle;
      localObject = new String((String)localObject);
      break;
      try
      {
        label54:
        JSONArray localJSONArray = new org/json/JSONArray;
        localJSONArray.<init>(str);
        localObject = new String[localJSONArray.length()];
        for (int i = 0;; i++)
        {
          paramBundle = (Bundle)localObject;
          if (i >= localObject.length) {
            break;
          }
          localObject[i] = localJSONArray.opt(i);
        }
        paramBundle = paramString.concat(paramBundle);
      }
      catch (JSONException paramBundle)
      {
        paramString = String.valueOf(paramString);
        paramBundle = String.valueOf("_loc_args");
        if (paramBundle.length() == 0) {}
      }
    }
    for (;;)
    {
      paramBundle = paramBundle.substring(6);
      Log.w("FirebaseMessaging", String.valueOf(paramBundle).length() + 41 + String.valueOf(str).length() + "Malformed " + paramBundle + ": " + str + "  Default value will be used.");
      paramBundle = null;
      break;
      paramBundle = new String(paramString);
    }
  }
  
  private final String zzd(Bundle paramBundle, String paramString)
  {
    String str = zza(paramBundle, paramString);
    if (!TextUtils.isEmpty(str)) {
      paramBundle = str;
    }
    for (;;)
    {
      return paramBundle;
      str = zzb(paramBundle, paramString);
      if (TextUtils.isEmpty(str))
      {
        paramBundle = null;
      }
      else
      {
        Resources localResources = this.zzqs.getResources();
        int i = localResources.getIdentifier(str, "string", this.zzqs.getPackageName());
        if (i == 0)
        {
          paramBundle = String.valueOf(paramString);
          paramString = String.valueOf("_loc_key");
          if (paramString.length() != 0) {}
          for (paramBundle = paramBundle.concat(paramString);; paramBundle = new String(paramBundle))
          {
            paramBundle = paramBundle.substring(6);
            Log.w("FirebaseMessaging", String.valueOf(paramBundle).length() + 49 + String.valueOf(str).length() + paramBundle + " resource not found: " + str + " Default value will be used.");
            paramBundle = null;
            break;
          }
        }
        paramString = zzc(paramBundle, paramString);
        if (paramString == null) {
          paramBundle = localResources.getString(i);
        } else {
          try
          {
            paramBundle = localResources.getString(i, paramString);
          }
          catch (MissingFormatArgumentException paramBundle)
          {
            paramString = Arrays.toString(paramString);
            Log.w("FirebaseMessaging", String.valueOf(str).length() + 58 + String.valueOf(paramString).length() + "Missing format argument for " + str + ": " + paramString + " Default value will be used.", paramBundle);
            paramBundle = null;
          }
        }
      }
    }
  }
  
  @TargetApi(26)
  private static Method zzfh(String paramString)
  {
    try
    {
      paramString = Notification.Builder.class.getMethod(paramString, new Class[] { String.class });
      return paramString;
    }
    catch (SecurityException paramString)
    {
      for (;;)
      {
        paramString = null;
      }
    }
    catch (NoSuchMethodException paramString)
    {
      for (;;) {}
    }
  }
  
  private final Integer zzfi(String paramString)
  {
    Object localObject = null;
    if (Build.VERSION.SDK_INT < 21) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      int i;
      if (!TextUtils.isEmpty(paramString))
      {
        try
        {
          i = Color.parseColor(paramString);
          paramString = Integer.valueOf(i);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          Log.w("FirebaseMessaging", String.valueOf(paramString).length() + 54 + "Color " + paramString + " not valid. Notification will use default color.");
        }
      }
      else
      {
        i = zzti().getInt("com.google.firebase.messaging.default_notification_color", 0);
        paramString = (String)localObject;
        if (i != 0) {
          try
          {
            i = ContextCompat.getColor(this.zzqs, i);
            paramString = Integer.valueOf(i);
          }
          catch (Resources.NotFoundException paramString)
          {
            Log.w("FirebaseMessaging", "Cannot find the color resource referenced in AndroidManifest.");
            paramString = (String)localObject;
          }
        }
      }
    }
  }
  
  @TargetApi(26)
  private final String zzfj(String paramString)
  {
    Object localObject;
    if (!PlatformVersion.isAtLeastO()) {
      localObject = null;
    }
    for (;;)
    {
      return (String)localObject;
      NotificationManager localNotificationManager = (NotificationManager)this.zzqs.getSystemService(NotificationManager.class);
      try
      {
        if (this.zzbsj == null) {
          this.zzbsj = localNotificationManager.getClass().getMethod("getNotificationChannel", new Class[] { String.class });
        }
        if (!TextUtils.isEmpty(paramString))
        {
          localObject = paramString;
          if (this.zzbsj.invoke(localNotificationManager, new Object[] { paramString }) != null) {
            continue;
          }
          int i = String.valueOf(paramString).length();
          localObject = new java/lang/StringBuilder;
          ((StringBuilder)localObject).<init>(i + 122);
          Log.w("FirebaseMessaging", "Notification Channel requested (" + paramString + ") has not been created by the app. Manifest configuration, or default, value will be used.");
        }
        paramString = zzti().getString("com.google.firebase.messaging.default_notification_channel_id");
        if (!TextUtils.isEmpty(paramString))
        {
          localObject = paramString;
          if (this.zzbsj.invoke(localNotificationManager, new Object[] { paramString }) != null) {
            continue;
          }
          Log.w("FirebaseMessaging", "Notification Channel set in AndroidManifest.xml has not been created by the app. Default value will be used.");
        }
        for (;;)
        {
          if (this.zzbsj.invoke(localNotificationManager, new Object[] { "fcm_fallback_notification_channel" }) == null)
          {
            localObject = Class.forName("android.app.NotificationChannel");
            paramString = ((Class)localObject).getConstructor(new Class[] { String.class, CharSequence.class, Integer.TYPE }).newInstance(new Object[] { "fcm_fallback_notification_channel", this.zzqs.getString(R.string.fcm_fallback_notification_channel_label), Integer.valueOf(3) });
            localNotificationManager.getClass().getMethod("createNotificationChannel", new Class[] { localObject }).invoke(localNotificationManager, new Object[] { paramString });
          }
          localObject = "fcm_fallback_notification_channel";
          break;
          Log.w("FirebaseMessaging", "Missing Default Notification Channel metadata in AndroidManifest. Default value will be used.");
        }
      }
      catch (InstantiationException paramString)
      {
        Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        localObject = null;
      }
      catch (InvocationTargetException paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
      catch (NoSuchMethodException paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
      catch (IllegalAccessException paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
      catch (ClassNotFoundException paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
      catch (SecurityException paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
      catch (IllegalArgumentException paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
      catch (LinkageError paramString)
      {
        for (;;)
        {
          Log.e("FirebaseMessaging", "Error while setting the notification channel", paramString);
        }
      }
    }
  }
  
  static boolean zzl(Bundle paramBundle)
  {
    if (("1".equals(zza(paramBundle, "gcm.n.e"))) || (zza(paramBundle, "gcm.n.icon") != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  static Uri zzm(Bundle paramBundle)
  {
    String str1 = zza(paramBundle, "gcm.n.link_android");
    String str2 = str1;
    if (TextUtils.isEmpty(str1)) {
      str2 = zza(paramBundle, "gcm.n.link");
    }
    if (!TextUtils.isEmpty(str2)) {}
    for (paramBundle = Uri.parse(str2);; paramBundle = null) {
      return paramBundle;
    }
  }
  
  static String zzo(Bundle paramBundle)
  {
    String str1 = zza(paramBundle, "gcm.n.sound2");
    String str2 = str1;
    if (TextUtils.isEmpty(str1)) {
      str2 = zza(paramBundle, "gcm.n.sound");
    }
    return str2;
  }
  
  private final Bundle zzti()
  {
    Object localObject;
    if (this.zzbsh != null) {
      localObject = this.zzbsh;
    }
    for (;;)
    {
      return (Bundle)localObject;
      localObject = null;
      try
      {
        ApplicationInfo localApplicationInfo = this.zzqs.getPackageManager().getApplicationInfo(this.zzqs.getPackageName(), 128);
        localObject = localApplicationInfo;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;) {}
      }
      if ((localObject != null) && (((ApplicationInfo)localObject).metaData != null))
      {
        this.zzbsh = ((ApplicationInfo)localObject).metaData;
        localObject = this.zzbsh;
      }
      else
      {
        localObject = Bundle.EMPTY;
      }
    }
  }
  
  static zza zzw(Context paramContext)
  {
    try
    {
      if (zzbsg == null)
      {
        zza localzza = new com/google/firebase/messaging/zza;
        localzza.<init>(paramContext);
        zzbsg = localzza;
      }
      paramContext = zzbsg;
      return paramContext;
    }
    finally {}
  }
  
  final boolean zzn(Bundle paramBundle)
  {
    Object localObject1 = null;
    boolean bool;
    if ("1".equals(zza(paramBundle, "gcm.n.noui")))
    {
      bool = true;
      return bool;
    }
    int i;
    Object localObject3;
    if (!((KeyguardManager)this.zzqs.getSystemService("keyguard")).inKeyguardRestrictedInputMode())
    {
      if (!PlatformVersion.isAtLeastLollipop()) {
        SystemClock.sleep(10L);
      }
      i = Process.myPid();
      localObject2 = ((ActivityManager)this.zzqs.getSystemService("activity")).getRunningAppProcesses();
      if (localObject2 != null)
      {
        localObject2 = ((List)localObject2).iterator();
        for (;;)
        {
          if (((Iterator)localObject2).hasNext())
          {
            localObject3 = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject2).next();
            if (((ActivityManager.RunningAppProcessInfo)localObject3).pid == i) {
              if (((ActivityManager.RunningAppProcessInfo)localObject3).importance == 100) {
                i = 1;
              }
            }
          }
        }
      }
    }
    for (;;)
    {
      if (i == 0) {
        break label157;
      }
      bool = false;
      break;
      i = 0;
      continue;
      i = 0;
    }
    label157:
    Object localObject2 = zzd(paramBundle, "gcm.n.title");
    Object localObject4 = localObject2;
    if (TextUtils.isEmpty((CharSequence)localObject2)) {
      localObject4 = this.zzqs.getApplicationInfo().loadLabel(this.zzqs.getPackageManager());
    }
    String str1 = zzd(paramBundle, "gcm.n.body");
    localObject2 = zza(paramBundle, "gcm.n.icon");
    label267:
    Integer localInteger;
    if (!TextUtils.isEmpty((CharSequence)localObject2))
    {
      localObject3 = this.zzqs.getResources();
      i = ((Resources)localObject3).getIdentifier((String)localObject2, "drawable", this.zzqs.getPackageName());
      if ((i != 0) && (zzaf(i)))
      {
        localInteger = zzfi(zza(paramBundle, "gcm.n.color"));
        localObject2 = zzo(paramBundle);
        if (!TextUtils.isEmpty((CharSequence)localObject2)) {
          break label744;
        }
        localObject2 = null;
        label297:
        localObject3 = zza(paramBundle, "gcm.n.click_action");
        if (TextUtils.isEmpty((CharSequence)localObject3)) {
          break label858;
        }
        localObject3 = new Intent((String)localObject3);
        ((Intent)localObject3).setPackage(this.zzqs.getPackageName());
        ((Intent)localObject3).setFlags(268435456);
        label347:
        if (localObject3 != null) {
          break label941;
        }
        localObject3 = null;
        label355:
        if (!FirebaseMessagingService.zzq(paramBundle)) {
          break label1196;
        }
        localObject1 = new Intent("com.google.firebase.messaging.NOTIFICATION_OPEN");
        zza((Intent)localObject1, paramBundle);
        ((Intent)localObject1).putExtra("pending_intent", (Parcelable)localObject3);
        localObject3 = zzz.zza(this.zzqs, this.zzbsk.incrementAndGet(), (Intent)localObject1, NUM);
        localObject1 = new Intent("com.google.firebase.messaging.NOTIFICATION_DISMISS");
        zza((Intent)localObject1, paramBundle);
        localObject1 = zzz.zza(this.zzqs, this.zzbsk.incrementAndGet(), (Intent)localObject1, NUM);
      }
    }
    label744:
    label858:
    label941:
    label1196:
    for (;;)
    {
      if ((PlatformVersion.isAtLeastO()) && (this.zzqs.getApplicationInfo().targetSdkVersion > 25)) {}
      Object localObject5;
      for (localObject2 = zza((CharSequence)localObject4, str1, i, localInteger, (Uri)localObject2, (PendingIntent)localObject3, (PendingIntent)localObject1, zzfj(zza(paramBundle, "gcm.n.android_channel_id")));; localObject2 = ((NotificationCompat.Builder)localObject5).build())
      {
        localObject3 = zza(paramBundle, "gcm.n.tag");
        if (Log.isLoggable("FirebaseMessaging", 3)) {
          Log.d("FirebaseMessaging", "Showing notification");
        }
        localObject4 = (NotificationManager)this.zzqs.getSystemService("notification");
        paramBundle = (Bundle)localObject3;
        if (TextUtils.isEmpty((CharSequence)localObject3))
        {
          long l = SystemClock.uptimeMillis();
          paramBundle = 37 + "FCM-Notification:" + l;
        }
        ((NotificationManager)localObject4).notify(paramBundle, 0, (Notification)localObject2);
        bool = true;
        break;
        int j = ((Resources)localObject3).getIdentifier((String)localObject2, "mipmap", this.zzqs.getPackageName());
        if (j != 0)
        {
          i = j;
          if (zzaf(j)) {
            break label267;
          }
        }
        Log.w("FirebaseMessaging", String.valueOf(localObject2).length() + 61 + "Icon resource " + (String)localObject2 + " not found. Notification will use default icon.");
        j = zzti().getInt("com.google.firebase.messaging.default_notification_icon", 0);
        if (j != 0)
        {
          i = j;
          if (zzaf(j)) {}
        }
        else
        {
          i = this.zzqs.getApplicationInfo().icon;
        }
        if (i != 0)
        {
          j = i;
          if (zzaf(i)) {}
        }
        else
        {
          j = 17301651;
        }
        i = j;
        break label267;
        if ((!"default".equals(localObject2)) && (this.zzqs.getResources().getIdentifier((String)localObject2, "raw", this.zzqs.getPackageName()) != 0))
        {
          localObject3 = this.zzqs.getPackageName();
          localObject2 = Uri.parse(String.valueOf(localObject3).length() + 24 + String.valueOf(localObject2).length() + "android.resource://" + (String)localObject3 + "/raw/" + (String)localObject2);
          break label297;
        }
        localObject2 = RingtoneManager.getDefaultUri(2);
        break label297;
        localObject5 = zzm(paramBundle);
        if (localObject5 != null)
        {
          localObject3 = new Intent("android.intent.action.VIEW");
          ((Intent)localObject3).setPackage(this.zzqs.getPackageName());
          ((Intent)localObject3).setData((Uri)localObject5);
          break label347;
        }
        localObject3 = this.zzqs.getPackageManager().getLaunchIntentForPackage(this.zzqs.getPackageName());
        if (localObject3 == null) {
          Log.w("FirebaseMessaging", "No activity found to launch app");
        }
        break label347;
        ((Intent)localObject3).addFlags(67108864);
        localObject5 = new Bundle(paramBundle);
        FirebaseMessagingService.zzp((Bundle)localObject5);
        ((Intent)localObject3).putExtras((Bundle)localObject5);
        localObject5 = ((Bundle)localObject5).keySet().iterator();
        while (((Iterator)localObject5).hasNext())
        {
          String str2 = (String)((Iterator)localObject5).next();
          if ((str2.startsWith("gcm.n.")) || (str2.startsWith("gcm.notification."))) {
            ((Intent)localObject3).removeExtra(str2);
          }
        }
        localObject3 = PendingIntent.getActivity(this.zzqs, this.zzbsk.incrementAndGet(), (Intent)localObject3, NUM);
        break label355;
        localObject5 = new NotificationCompat.Builder(this.zzqs).setAutoCancel(true).setSmallIcon(i);
        if (!TextUtils.isEmpty((CharSequence)localObject4)) {
          ((NotificationCompat.Builder)localObject5).setContentTitle((CharSequence)localObject4);
        }
        if (!TextUtils.isEmpty(str1))
        {
          ((NotificationCompat.Builder)localObject5).setContentText(str1);
          ((NotificationCompat.Builder)localObject5).setStyle(new NotificationCompat.BigTextStyle().bigText(str1));
        }
        if (localInteger != null) {
          ((NotificationCompat.Builder)localObject5).setColor(localInteger.intValue());
        }
        if (localObject2 != null) {
          ((NotificationCompat.Builder)localObject5).setSound((Uri)localObject2);
        }
        if (localObject3 != null) {
          ((NotificationCompat.Builder)localObject5).setContentIntent((PendingIntent)localObject3);
        }
        if (localObject1 != null) {
          ((NotificationCompat.Builder)localObject5).setDeleteIntent((PendingIntent)localObject1);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/messaging/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */