package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.util.DeviceProperties;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import javax.annotation.concurrent.GuardedBy;

public final class ConnectionErrorMessages
{
  @GuardedBy("sCache")
  private static final SimpleArrayMap<String, String> zzse = new SimpleArrayMap();
  
  public static String getAppName(Context paramContext)
  {
    str1 = paramContext.getPackageName();
    try
    {
      String str2 = Wrappers.packageManager(paramContext).getApplicationLabel(str1).toString();
      paramContext = str2;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        String str3 = paramContext.getApplicationInfo().name;
        paramContext = str1;
        if (!TextUtils.isEmpty(str3)) {
          paramContext = str3;
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
    }
    return paramContext;
  }
  
  public static String getDefaultNotificationChannelName(Context paramContext)
  {
    return paramContext.getResources().getString(com.google.android.gms.base.R.string.common_google_play_services_notification_channel_name);
  }
  
  public static String getErrorDialogButtonMessage(Context paramContext, int paramInt)
  {
    paramContext = paramContext.getResources();
    switch (paramInt)
    {
    default: 
      paramContext = paramContext.getString(17039370);
    }
    for (;;)
    {
      return paramContext;
      paramContext = paramContext.getString(com.google.android.gms.base.R.string.common_google_play_services_install_button);
      continue;
      paramContext = paramContext.getString(com.google.android.gms.base.R.string.common_google_play_services_enable_button);
      continue;
      paramContext = paramContext.getString(com.google.android.gms.base.R.string.common_google_play_services_update_button);
    }
  }
  
  public static String getErrorMessage(Context paramContext, int paramInt)
  {
    Resources localResources = paramContext.getResources();
    String str = getAppName(paramContext);
    switch (paramInt)
    {
    case 4: 
    case 6: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 19: 
    default: 
      paramContext = localResources.getString(com.google.android.gms.common.R.string.common_google_play_services_unknown_issue, new Object[] { str });
    }
    for (;;)
    {
      return paramContext;
      paramContext = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_install_text, new Object[] { str });
      continue;
      paramContext = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_enable_text, new Object[] { str });
      continue;
      paramContext = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_updating_text, new Object[] { str });
      continue;
      if (DeviceProperties.isWearableWithoutPlayStore(paramContext))
      {
        paramContext = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_wear_update_text);
      }
      else
      {
        paramContext = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_update_text, new Object[] { str });
        continue;
        paramContext = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_unsupported_text, new Object[] { str });
        continue;
        paramContext = zza(paramContext, "common_google_play_services_network_error_text", str);
        continue;
        paramContext = zza(paramContext, "common_google_play_services_invalid_account_text", str);
        continue;
        paramContext = zza(paramContext, "common_google_play_services_api_unavailable_text", str);
        continue;
        paramContext = zza(paramContext, "common_google_play_services_sign_in_failed_text", str);
        continue;
        paramContext = zza(paramContext, "common_google_play_services_restricted_profile_text", str);
      }
    }
  }
  
  public static String getErrorNotificationMessage(Context paramContext, int paramInt)
  {
    if (paramInt == 6) {}
    for (paramContext = zza(paramContext, "common_google_play_services_resolution_required_text", getAppName(paramContext));; paramContext = getErrorMessage(paramContext, paramInt)) {
      return paramContext;
    }
  }
  
  public static String getErrorNotificationTitle(Context paramContext, int paramInt)
  {
    if (paramInt == 6) {}
    for (String str1 = zzb(paramContext, "common_google_play_services_resolution_required_title");; str1 = getErrorTitle(paramContext, paramInt))
    {
      String str2 = str1;
      if (str1 == null) {
        str2 = paramContext.getResources().getString(com.google.android.gms.base.R.string.common_google_play_services_notification_ticker);
      }
      return str2;
    }
  }
  
  public static String getErrorTitle(Context paramContext, int paramInt)
  {
    Object localObject1 = null;
    Resources localResources = paramContext.getResources();
    Object localObject2 = localObject1;
    switch (paramInt)
    {
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 19: 
    default: 
      Log.e("GoogleApiAvailability", 33 + "Unexpected error code " + paramInt);
      localObject2 = localObject1;
    }
    for (;;)
    {
      return (String)localObject2;
      localObject2 = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_install_title);
      continue;
      localObject2 = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_enable_title);
      continue;
      localObject2 = localResources.getString(com.google.android.gms.base.R.string.common_google_play_services_update_title);
      continue;
      Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
      localObject2 = localObject1;
      continue;
      Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
      localObject2 = zzb(paramContext, "common_google_play_services_network_error_title");
      continue;
      Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
      localObject2 = localObject1;
      continue;
      Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
      localObject2 = localObject1;
      continue;
      Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
      localObject2 = zzb(paramContext, "common_google_play_services_invalid_account_title");
      continue;
      Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
      localObject2 = localObject1;
      continue;
      Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
      localObject2 = localObject1;
      continue;
      Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
      localObject2 = zzb(paramContext, "common_google_play_services_sign_in_failed_title");
      continue;
      Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
      localObject2 = zzb(paramContext, "common_google_play_services_restricted_profile_title");
    }
  }
  
  private static String zza(Context paramContext, String paramString1, String paramString2)
  {
    Resources localResources = paramContext.getResources();
    paramString1 = zzb(paramContext, paramString1);
    paramContext = paramString1;
    if (paramString1 == null) {
      paramContext = localResources.getString(com.google.android.gms.common.R.string.common_google_play_services_unknown_issue);
    }
    return String.format(localResources.getConfiguration().locale, paramContext, new Object[] { paramString2 });
  }
  
  private static String zzb(Context paramContext, String paramString)
  {
    for (;;)
    {
      int i;
      synchronized (zzse)
      {
        String str = (String)zzse.get(paramString);
        if (str != null)
        {
          paramContext = str;
          return paramContext;
        }
        paramContext = GooglePlayServicesUtil.getRemoteResource(paramContext);
        if (paramContext == null)
        {
          paramContext = null;
          continue;
        }
        i = paramContext.getIdentifier(paramString, "string", "com.google.android.gms");
        if (i == 0)
        {
          paramContext = String.valueOf(paramString);
          if (paramContext.length() != 0)
          {
            paramContext = "Missing resource: ".concat(paramContext);
            Log.w("GoogleApiAvailability", paramContext);
            paramContext = null;
            continue;
          }
          paramContext = new String("Missing resource: ");
        }
      }
      paramContext = paramContext.getString(i);
      if (TextUtils.isEmpty(paramContext))
      {
        paramContext = String.valueOf(paramString);
        if (paramContext.length() != 0) {}
        for (paramContext = "Got empty resource: ".concat(paramContext);; paramContext = new String("Got empty resource: "))
        {
          Log.w("GoogleApiAvailability", paramContext);
          paramContext = null;
          break;
        }
      }
      zzse.put(paramString, paramContext);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ConnectionErrorMessages.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */