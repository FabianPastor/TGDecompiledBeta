package com.google.android.gms.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.base.R.drawable;
import com.google.android.gms.base.R.string;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.internal.GooglePlayServicesUpdatedReceiver;
import com.google.android.gms.common.api.internal.GooglePlayServicesUpdatedReceiver.Callback;
import com.google.android.gms.common.api.internal.LifecycleFragment;
import com.google.android.gms.common.internal.ConnectionErrorMessages;
import com.google.android.gms.common.internal.DialogRedirect;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.DeviceProperties;
import com.google.android.gms.common.util.PlatformVersion;
import java.util.concurrent.atomic.AtomicBoolean;

public class GoogleApiAvailability
  extends GoogleApiAvailabilityLight
{
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  private static final Object mLock = new Object();
  private static final GoogleApiAvailability zzas = new GoogleApiAvailability();
  private String zzat;
  
  public static GoogleApiAvailability getInstance()
  {
    return zzas;
  }
  
  static Dialog zza(Context paramContext, int paramInt, DialogRedirect paramDialogRedirect, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    AlertDialog.Builder localBuilder = null;
    Object localObject = null;
    if (paramInt == 0) {}
    for (paramContext = (Context)localObject;; paramContext = ((AlertDialog.Builder)localObject).create())
    {
      return paramContext;
      localObject = new TypedValue();
      paramContext.getTheme().resolveAttribute(16843529, (TypedValue)localObject, true);
      if ("Theme.Dialog.Alert".equals(paramContext.getResources().getResourceEntryName(((TypedValue)localObject).resourceId))) {
        localBuilder = new AlertDialog.Builder(paramContext, 5);
      }
      localObject = localBuilder;
      if (localBuilder == null) {
        localObject = new AlertDialog.Builder(paramContext);
      }
      ((AlertDialog.Builder)localObject).setMessage(ConnectionErrorMessages.getErrorMessage(paramContext, paramInt));
      if (paramOnCancelListener != null) {
        ((AlertDialog.Builder)localObject).setOnCancelListener(paramOnCancelListener);
      }
      paramOnCancelListener = ConnectionErrorMessages.getErrorDialogButtonMessage(paramContext, paramInt);
      if (paramOnCancelListener != null) {
        ((AlertDialog.Builder)localObject).setPositiveButton(paramOnCancelListener, paramDialogRedirect);
      }
      paramContext = ConnectionErrorMessages.getErrorTitle(paramContext, paramInt);
      if (paramContext != null) {
        ((AlertDialog.Builder)localObject).setTitle(paramContext);
      }
    }
  }
  
  @TargetApi(26)
  private final String zza(Context paramContext, NotificationManager paramNotificationManager)
  {
    Preconditions.checkState(PlatformVersion.isAtLeastO());
    String str1 = zzb();
    String str2 = str1;
    NotificationChannel localNotificationChannel;
    if (str1 == null)
    {
      str1 = "com.google.android.gms.availability";
      localNotificationChannel = paramNotificationManager.getNotificationChannel("com.google.android.gms.availability");
      paramContext = ConnectionErrorMessages.getDefaultNotificationChannelName(paramContext);
      if (localNotificationChannel != null) {
        break label60;
      }
      paramNotificationManager.createNotificationChannel(new NotificationChannel("com.google.android.gms.availability", paramContext, 4));
      str2 = str1;
    }
    for (;;)
    {
      return str2;
      label60:
      str2 = str1;
      if (!paramContext.equals(localNotificationChannel.getName()))
      {
        localNotificationChannel.setName(paramContext);
        paramNotificationManager.createNotificationChannel(localNotificationChannel);
        str2 = str1;
      }
    }
  }
  
  static void zza(Activity paramActivity, Dialog paramDialog, String paramString, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    if ((paramActivity instanceof FragmentActivity))
    {
      paramActivity = ((FragmentActivity)paramActivity).getSupportFragmentManager();
      SupportErrorDialogFragment.newInstance(paramDialog, paramOnCancelListener).show(paramActivity, paramString);
    }
    for (;;)
    {
      return;
      paramActivity = paramActivity.getFragmentManager();
      ErrorDialogFragment.newInstance(paramDialog, paramOnCancelListener).show(paramActivity, paramString);
    }
  }
  
  @TargetApi(20)
  private final void zza(Context paramContext, int paramInt, String paramString, PendingIntent paramPendingIntent)
  {
    if (paramInt == 18) {
      zza(paramContext);
    }
    for (;;)
    {
      return;
      if (paramPendingIntent == null)
      {
        if (paramInt == 6) {
          Log.w("GoogleApiAvailability", "Missing resolution for ConnectionResult.RESOLUTION_REQUIRED. Call GoogleApiAvailability#showErrorNotification(Context, ConnectionResult) instead.");
        }
      }
      else
      {
        String str = ConnectionErrorMessages.getErrorNotificationTitle(paramContext, paramInt);
        Object localObject = ConnectionErrorMessages.getErrorNotificationMessage(paramContext, paramInt);
        Resources localResources = paramContext.getResources();
        NotificationManager localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
        if (DeviceProperties.isWearable(paramContext))
        {
          Preconditions.checkState(PlatformVersion.isAtLeastKitKatWatch());
          localObject = new Notification.Builder(paramContext).setSmallIcon(paramContext.getApplicationInfo().icon).setPriority(2).setAutoCancel(true).setContentTitle(str).setStyle(new Notification.BigTextStyle().bigText((CharSequence)localObject));
          if (DeviceProperties.isWearableWithoutPlayStore(paramContext))
          {
            ((Notification.Builder)localObject).addAction(R.drawable.common_full_open_on_phone, localResources.getString(R.string.common_open_on_phone), paramPendingIntent);
            label152:
            if ((PlatformVersion.isAtLeastO()) && (PlatformVersion.isAtLeastO())) {
              ((Notification.Builder)localObject).setChannelId(zza(paramContext, localNotificationManager));
            }
            paramContext = ((Notification.Builder)localObject).build();
            switch (paramInt)
            {
            default: 
              label183:
              paramInt = 39789;
            }
          }
        }
        for (;;)
        {
          if (paramString != null) {
            break label360;
          }
          localNotificationManager.notify(paramInt, paramContext);
          break;
          ((Notification.Builder)localObject).setContentIntent(paramPendingIntent);
          break label152;
          paramPendingIntent = new NotificationCompat.Builder(paramContext).setSmallIcon(17301642).setTicker(localResources.getString(R.string.common_google_play_services_notification_ticker)).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentIntent(paramPendingIntent).setContentTitle(str).setContentText((CharSequence)localObject).setLocalOnly(true).setStyle(new NotificationCompat.BigTextStyle().bigText((CharSequence)localObject));
          if ((PlatformVersion.isAtLeastO()) && (PlatformVersion.isAtLeastO())) {
            paramPendingIntent.setChannelId(zza(paramContext, localNotificationManager));
          }
          paramContext = paramPendingIntent.build();
          break label183;
          paramInt = 10436;
          GooglePlayServicesUtilLight.zzbt.set(false);
        }
        label360:
        localNotificationManager.notify(paramString, paramInt, paramContext);
      }
    }
  }
  
  private final String zzb()
  {
    synchronized (mLock)
    {
      String str = this.zzat;
      return str;
    }
  }
  
  public int getApkVersion(Context paramContext)
  {
    return super.getApkVersion(paramContext);
  }
  
  public Dialog getErrorDialog(Activity paramActivity, int paramInt1, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    return zza(paramActivity, paramInt1, DialogRedirect.getInstance(paramActivity, getErrorResolutionIntent(paramActivity, paramInt1, "d"), paramInt2), paramOnCancelListener);
  }
  
  @Deprecated
  public Intent getErrorResolutionIntent(int paramInt)
  {
    return super.getErrorResolutionIntent(paramInt);
  }
  
  public Intent getErrorResolutionIntent(Context paramContext, int paramInt, String paramString)
  {
    return super.getErrorResolutionIntent(paramContext, paramInt, paramString);
  }
  
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, int paramInt1, int paramInt2)
  {
    return super.getErrorResolutionPendingIntent(paramContext, paramInt1, paramInt2);
  }
  
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, int paramInt1, int paramInt2, String paramString)
  {
    return super.getErrorResolutionPendingIntent(paramContext, paramInt1, paramInt2, paramString);
  }
  
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, ConnectionResult paramConnectionResult)
  {
    if (paramConnectionResult.hasResolution()) {}
    for (paramContext = paramConnectionResult.getResolution();; paramContext = getErrorResolutionPendingIntent(paramContext, paramConnectionResult.getErrorCode(), 0)) {
      return paramContext;
    }
  }
  
  public final String getErrorString(int paramInt)
  {
    return super.getErrorString(paramInt);
  }
  
  public int isGooglePlayServicesAvailable(Context paramContext)
  {
    return super.isGooglePlayServicesAvailable(paramContext);
  }
  
  public int isGooglePlayServicesAvailable(Context paramContext, int paramInt)
  {
    return super.isGooglePlayServicesAvailable(paramContext, paramInt);
  }
  
  public boolean isPlayServicesPossiblyUpdating(Context paramContext, int paramInt)
  {
    return super.isPlayServicesPossiblyUpdating(paramContext, paramInt);
  }
  
  public final boolean isUserResolvableError(int paramInt)
  {
    return super.isUserResolvableError(paramInt);
  }
  
  public GooglePlayServicesUpdatedReceiver registerCallbackOnUpdate(Context paramContext, GooglePlayServicesUpdatedReceiver.Callback paramCallback)
  {
    Object localObject = new IntentFilter("android.intent.action.PACKAGE_ADDED");
    ((IntentFilter)localObject).addDataScheme("package");
    GooglePlayServicesUpdatedReceiver localGooglePlayServicesUpdatedReceiver = new GooglePlayServicesUpdatedReceiver(paramCallback);
    paramContext.registerReceiver(localGooglePlayServicesUpdatedReceiver, (IntentFilter)localObject);
    localGooglePlayServicesUpdatedReceiver.zzc(paramContext);
    localObject = localGooglePlayServicesUpdatedReceiver;
    if (!isUninstalledAppPossiblyUpdating(paramContext, "com.google.android.gms"))
    {
      paramCallback.zzv();
      localGooglePlayServicesUpdatedReceiver.unregister();
      localObject = null;
    }
    return (GooglePlayServicesUpdatedReceiver)localObject;
  }
  
  public boolean showErrorDialogFragment(Activity paramActivity, int paramInt1, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    Dialog localDialog = getErrorDialog(paramActivity, paramInt1, paramInt2, paramOnCancelListener);
    if (localDialog == null) {}
    for (boolean bool = false;; bool = true)
    {
      return bool;
      zza(paramActivity, localDialog, "GooglePlayServicesErrorDialog", paramOnCancelListener);
    }
  }
  
  public boolean showErrorDialogFragment(Activity paramActivity, LifecycleFragment paramLifecycleFragment, int paramInt1, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    paramLifecycleFragment = zza(paramActivity, paramInt1, DialogRedirect.getInstance(paramLifecycleFragment, getErrorResolutionIntent(paramActivity, paramInt1, "d"), paramInt2), paramOnCancelListener);
    if (paramLifecycleFragment == null) {}
    for (boolean bool = false;; bool = true)
    {
      return bool;
      zza(paramActivity, paramLifecycleFragment, "GooglePlayServicesErrorDialog", paramOnCancelListener);
    }
  }
  
  public void showErrorNotification(Context paramContext, int paramInt)
  {
    showErrorNotification(paramContext, paramInt, null);
  }
  
  public void showErrorNotification(Context paramContext, int paramInt, String paramString)
  {
    zza(paramContext, paramInt, paramString, getErrorResolutionPendingIntent(paramContext, paramInt, 0, "n"));
  }
  
  public Dialog showUpdatingDialog(Activity paramActivity, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    Object localObject = new ProgressBar(paramActivity, null, 16842874);
    ((ProgressBar)localObject).setIndeterminate(true);
    ((ProgressBar)localObject).setVisibility(0);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramActivity);
    localBuilder.setView((View)localObject);
    localBuilder.setMessage(ConnectionErrorMessages.getErrorMessage(paramActivity, 18));
    localBuilder.setPositiveButton("", null);
    localObject = localBuilder.create();
    zza(paramActivity, (Dialog)localObject, "GooglePlayServicesUpdatingDialog", paramOnCancelListener);
    return (Dialog)localObject;
  }
  
  public boolean showWrappedErrorNotification(Context paramContext, ConnectionResult paramConnectionResult, int paramInt)
  {
    PendingIntent localPendingIntent = getErrorResolutionPendingIntent(paramContext, paramConnectionResult);
    if (localPendingIntent != null) {
      zza(paramContext, paramConnectionResult.getErrorCode(), null, GoogleApiActivity.zza(paramContext, localPendingIntent, paramInt));
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  final void zza(Context paramContext)
  {
    new zza(paramContext).sendEmptyMessageDelayed(1, 120000L);
  }
  
  @SuppressLint({"HandlerLeak"})
  private final class zza
    extends Handler
  {
    private final Context zzau;
    
    public zza(Context paramContext) {}
    
    public final void handleMessage(Message paramMessage)
    {
      int i;
      switch (paramMessage.what)
      {
      default: 
        i = paramMessage.what;
        Log.w("GoogleApiAvailability", 50 + "Don't know how to handle this message: " + i);
      }
      for (;;)
      {
        return;
        i = GoogleApiAvailability.this.isGooglePlayServicesAvailable(this.zzau);
        if (GoogleApiAvailability.this.isUserResolvableError(i)) {
          GoogleApiAvailability.this.showErrorNotification(this.zzau, i);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GoogleApiAvailability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */