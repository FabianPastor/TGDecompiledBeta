package com.google.android.gms.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.google.android.gms.R.drawable;
import com.google.android.gms.R.string;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.util.zzs;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GooglePlayServicesUtil
  extends zze
{
  public static final String GMS_ERROR_DIALOG = "GooglePlayServicesErrorDialog";
  @Deprecated
  public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
  @Deprecated
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  public static final String GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending";
  
  @Deprecated
  public static Dialog getErrorDialog(int paramInt1, Activity paramActivity, int paramInt2)
  {
    return getErrorDialog(paramInt1, paramActivity, paramInt2, null);
  }
  
  @Deprecated
  public static Dialog getErrorDialog(int paramInt1, Activity paramActivity, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    int i = paramInt1;
    if (zzd(paramActivity, paramInt1)) {
      i = 18;
    }
    return GoogleApiAvailability.getInstance().getErrorDialog(paramActivity, i, paramInt2, paramOnCancelListener);
  }
  
  @Deprecated
  public static PendingIntent getErrorPendingIntent(int paramInt1, Context paramContext, int paramInt2)
  {
    return zze.getErrorPendingIntent(paramInt1, paramContext, paramInt2);
  }
  
  @Deprecated
  public static String getErrorString(int paramInt)
  {
    return zze.getErrorString(paramInt);
  }
  
  @Deprecated
  public static String getOpenSourceSoftwareLicenseInfo(Context paramContext)
  {
    return zze.getOpenSourceSoftwareLicenseInfo(paramContext);
  }
  
  public static Context getRemoteContext(Context paramContext)
  {
    return zze.getRemoteContext(paramContext);
  }
  
  public static Resources getRemoteResource(Context paramContext)
  {
    return zze.getRemoteResource(paramContext);
  }
  
  @Deprecated
  public static int isGooglePlayServicesAvailable(Context paramContext)
  {
    return zze.isGooglePlayServicesAvailable(paramContext);
  }
  
  @Deprecated
  public static boolean isUserRecoverableError(int paramInt)
  {
    return zze.isUserRecoverableError(paramInt);
  }
  
  @Deprecated
  public static boolean showErrorDialogFragment(int paramInt1, Activity paramActivity, int paramInt2)
  {
    return showErrorDialogFragment(paramInt1, paramActivity, paramInt2, null);
  }
  
  @Deprecated
  public static boolean showErrorDialogFragment(int paramInt1, Activity paramActivity, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    return showErrorDialogFragment(paramInt1, paramActivity, null, paramInt2, paramOnCancelListener);
  }
  
  public static boolean showErrorDialogFragment(int paramInt1, Activity paramActivity, Fragment paramFragment, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    int i = paramInt1;
    if (zzd(paramActivity, paramInt1)) {
      i = 18;
    }
    GoogleApiAvailability localGoogleApiAvailability = GoogleApiAvailability.getInstance();
    if (paramFragment == null) {
      return localGoogleApiAvailability.showErrorDialogFragment(paramActivity, i, paramInt2, paramOnCancelListener);
    }
    paramFragment = localGoogleApiAvailability.zza(paramActivity, i, zzj.zza(paramFragment, GoogleApiAvailability.getInstance().zza(paramActivity, i, "d"), paramInt2), paramOnCancelListener);
    if (paramFragment == null) {
      return false;
    }
    localGoogleApiAvailability.zza(paramActivity, paramFragment, "GooglePlayServicesErrorDialog", paramOnCancelListener);
    return true;
  }
  
  @Deprecated
  public static void showErrorNotification(int paramInt, Context paramContext)
  {
    int i = paramInt;
    if (com.google.android.gms.common.util.zzi.zzcl(paramContext))
    {
      i = paramInt;
      if (paramInt == 2) {
        i = 42;
      }
    }
    if ((zzd(paramContext, i)) || (zze(paramContext, i)))
    {
      zzbs(paramContext);
      return;
    }
    zza(i, paramContext);
  }
  
  private static void zza(int paramInt, Context paramContext)
  {
    zza(paramInt, paramContext, null);
  }
  
  static void zza(int paramInt, Context paramContext, PendingIntent paramPendingIntent)
  {
    zza(paramInt, paramContext, null, paramPendingIntent);
  }
  
  private static void zza(int paramInt, Context paramContext, String paramString)
  {
    zza(paramInt, paramContext, paramString, GoogleApiAvailability.getInstance().zza(paramContext, paramInt, 0, "n"));
  }
  
  @TargetApi(20)
  private static void zza(int paramInt, Context paramContext, String paramString, PendingIntent paramPendingIntent)
  {
    Object localObject = paramContext.getResources();
    String str2 = com.google.android.gms.common.internal.zzi.zzh(paramContext, paramInt);
    String str1 = com.google.android.gms.common.internal.zzi.zzj(paramContext, paramInt);
    if (com.google.android.gms.common.util.zzi.zzcl(paramContext))
    {
      zzac.zzbr(zzs.zzaxo());
      paramPendingIntent = new Notification.Builder(paramContext).setSmallIcon(R.drawable.common_ic_googleplayservices).setPriority(2).setAutoCancel(true).setStyle(new Notification.BigTextStyle().bigText(String.valueOf(str2).length() + 1 + String.valueOf(str1).length() + str2 + " " + str1)).addAction(R.drawable.common_full_open_on_phone, ((Resources)localObject).getString(R.string.common_open_on_phone), paramPendingIntent).build();
      if (!zzfn(paramInt)) {
        break label345;
      }
      vd.set(false);
    }
    label345:
    for (paramInt = 10436;; paramInt = 39789)
    {
      paramContext = (NotificationManager)paramContext.getSystemService("notification");
      if (paramString == null) {
        break label352;
      }
      paramContext.notify(paramString, paramInt, paramPendingIntent);
      return;
      localObject = ((Resources)localObject).getString(R.string.common_google_play_services_notification_ticker);
      if (zzs.zzaxk())
      {
        paramPendingIntent = new Notification.Builder(paramContext).setSmallIcon(17301642).setContentTitle(str2).setContentText(str1).setContentIntent(paramPendingIntent).setTicker((CharSequence)localObject).setAutoCancel(true);
        if (zzs.zzaxs()) {
          paramPendingIntent.setLocalOnly(true);
        }
        if (zzs.zzaxo()) {
          paramPendingIntent.setStyle(new Notification.BigTextStyle().bigText(str1));
        }
        for (paramPendingIntent = paramPendingIntent.build();; paramPendingIntent = paramPendingIntent.getNotification())
        {
          if (Build.VERSION.SDK_INT == 19) {
            paramPendingIntent.extras.putBoolean("android.support.localOnly", true);
          }
          break;
        }
      }
      paramPendingIntent = new NotificationCompat.Builder(paramContext).setSmallIcon(17301642).setTicker((CharSequence)localObject).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentIntent(paramPendingIntent).setContentTitle(str2).setContentText(str1).build();
      break;
    }
    label352:
    paramContext.notify(paramInt, paramPendingIntent);
  }
  
  private static void zzbs(Context paramContext)
  {
    paramContext = new zza(paramContext);
    paramContext.sendMessageDelayed(paramContext.obtainMessage(1), 120000L);
  }
  
  @Deprecated
  public static boolean zzd(Context paramContext, int paramInt)
  {
    return zze.zzd(paramContext, paramInt);
  }
  
  @Deprecated
  public static boolean zze(Context paramContext, int paramInt)
  {
    return zze.zze(paramContext, paramInt);
  }
  
  @Deprecated
  public static Intent zzfm(int paramInt)
  {
    return zze.zzfm(paramInt);
  }
  
  private static class zza
    extends Handler
  {
    private final Context zzask;
    
    zza(Context paramContext) {}
    
    public void handleMessage(Message paramMessage)
    {
      int i;
      switch (paramMessage.what)
      {
      default: 
        i = paramMessage.what;
        Log.w("GooglePlayServicesUtil", 50 + "Don't know how to handle this message: " + i);
      }
      do
      {
        return;
        i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzask);
      } while (!GooglePlayServicesUtil.isUserRecoverableError(i));
      GooglePlayServicesUtil.zzb(i, this.zzask);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GooglePlayServicesUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */