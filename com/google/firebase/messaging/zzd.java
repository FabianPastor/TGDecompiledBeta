package com.google.firebase.messaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.measurement.AppMeasurement;

final class zzd
{
  private static void zzb(Context paramContext, String paramString, Intent paramIntent)
  {
    Bundle localBundle = new Bundle();
    String str = paramIntent.getStringExtra("google.c.a.c_id");
    if (str != null) {
      localBundle.putString("_nmid", str);
    }
    str = paramIntent.getStringExtra("google.c.a.c_l");
    if (str != null) {
      localBundle.putString("_nmn", str);
    }
    str = paramIntent.getStringExtra("google.c.a.m_l");
    if (!TextUtils.isEmpty(str)) {
      localBundle.putString("label", str);
    }
    str = paramIntent.getStringExtra("from");
    if ((str != null) && (str.startsWith("/topics/"))) {}
    for (;;)
    {
      if (str != null) {
        localBundle.putString("_nt", str);
      }
      try
      {
        localBundle.putInt("_nmt", Integer.parseInt(paramIntent.getStringExtra("google.c.a.ts")));
        if (!paramIntent.hasExtra("google.c.a.udt")) {}
      }
      catch (NumberFormatException localNumberFormatException)
      {
        try
        {
          localBundle.putInt("_ndt", Integer.parseInt(paramIntent.getStringExtra("google.c.a.udt")));
          if (Log.isLoggable("FirebaseMessaging", 3))
          {
            paramIntent = String.valueOf(localBundle);
            Log.d("FirebaseMessaging", String.valueOf(paramString).length() + 22 + String.valueOf(paramIntent).length() + "Sending event=" + paramString + " params=" + paramIntent);
          }
          paramContext = zzx(paramContext);
          if (paramContext != null)
          {
            paramContext.logEventInternal("fcm", paramString, localBundle);
            return;
            str = null;
            continue;
            localNumberFormatException = localNumberFormatException;
            Log.w("FirebaseMessaging", "Error while parsing timestamp in GCM event", localNumberFormatException);
          }
        }
        catch (NumberFormatException paramIntent)
        {
          for (;;)
          {
            Log.w("FirebaseMessaging", "Error while parsing use_device_time in GCM event", paramIntent);
            continue;
            Log.w("FirebaseMessaging", "Unable to log event: analytics library is missing");
          }
        }
      }
    }
  }
  
  public static void zzc(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("google.c.a.abt");
    if (str != null) {
      zzc.zza(paramContext, "fcm", Base64.decode(str, 0), new zzb(), 1);
    }
    zzb(paramContext, "_nr", paramIntent);
  }
  
  public static void zzd(Context paramContext, Intent paramIntent)
  {
    if (paramIntent != null)
    {
      if (!"1".equals(paramIntent.getStringExtra("google.c.a.tc"))) {
        break label124;
      }
      AppMeasurement localAppMeasurement = zzx(paramContext);
      if (Log.isLoggable("FirebaseMessaging", 3)) {
        Log.d("FirebaseMessaging", "Received event with track-conversion=true. Setting user property and reengagement event");
      }
      if (localAppMeasurement == null) {
        break label113;
      }
      String str = paramIntent.getStringExtra("google.c.a.c_id");
      localAppMeasurement.setUserPropertyInternal("fcm", "_ln", str);
      Bundle localBundle = new Bundle();
      localBundle.putString("source", "Firebase");
      localBundle.putString("medium", "notification");
      localBundle.putString("campaign", str);
      localAppMeasurement.logEventInternal("fcm", "_cmp", localBundle);
    }
    for (;;)
    {
      zzb(paramContext, "_no", paramIntent);
      return;
      label113:
      Log.w("FirebaseMessaging", "Unable to set user property for conversion tracking:  analytics library is missing");
      continue;
      label124:
      if (Log.isLoggable("FirebaseMessaging", 3)) {
        Log.d("FirebaseMessaging", "Received event with track-conversion=false. Do not set user property");
      }
    }
  }
  
  public static void zze(Context paramContext, Intent paramIntent)
  {
    zzb(paramContext, "_nd", paramIntent);
  }
  
  public static void zzf(Context paramContext, Intent paramIntent)
  {
    zzb(paramContext, "_nf", paramIntent);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/messaging/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */