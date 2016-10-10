package com.google.android.gms.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.internal.zzqv;
import com.google.android.gms.internal.zzqv.zza;
import com.google.android.gms.internal.zzrb;
import com.google.android.gms.internal.zzrf;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class GoogleApiAvailability
  extends zzc
{
  public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  private static final GoogleApiAvailability uM = new GoogleApiAvailability();
  
  public static GoogleApiAvailability getInstance()
  {
    return uM;
  }
  
  public Dialog getErrorDialog(Activity paramActivity, int paramInt1, int paramInt2)
  {
    return getErrorDialog(paramActivity, paramInt1, paramInt2, null);
  }
  
  public Dialog getErrorDialog(Activity paramActivity, int paramInt1, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    return zza(paramActivity, paramInt1, zzj.zza(paramActivity, zza(paramActivity, paramInt1, "d"), paramInt2), paramOnCancelListener);
  }
  
  @Nullable
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, int paramInt1, int paramInt2)
  {
    return super.getErrorResolutionPendingIntent(paramContext, paramInt1, paramInt2);
  }
  
  @Nullable
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, ConnectionResult paramConnectionResult)
  {
    if (paramConnectionResult.hasResolution()) {
      return paramConnectionResult.getResolution();
    }
    int j = paramConnectionResult.getErrorCode();
    int i = j;
    if (com.google.android.gms.common.util.zzi.zzcl(paramContext))
    {
      i = j;
      if (j == 2) {
        i = 42;
      }
    }
    return getErrorResolutionPendingIntent(paramContext, i, 0);
  }
  
  public final String getErrorString(int paramInt)
  {
    return super.getErrorString(paramInt);
  }
  
  @Nullable
  public String getOpenSourceSoftwareLicenseInfo(Context paramContext)
  {
    return super.getOpenSourceSoftwareLicenseInfo(paramContext);
  }
  
  public int isGooglePlayServicesAvailable(Context paramContext)
  {
    return super.isGooglePlayServicesAvailable(paramContext);
  }
  
  public final boolean isUserResolvableError(int paramInt)
  {
    return super.isUserResolvableError(paramInt);
  }
  
  @MainThread
  public Task<Void> makeGooglePlayServicesAvailable(Activity paramActivity)
  {
    zzac.zzhq("makeGooglePlayServicesAvailable must be called from the main thread");
    int i = isGooglePlayServicesAvailable(paramActivity);
    if (i == 0) {
      return Tasks.forResult(null);
    }
    paramActivity = zzrf.zzu(paramActivity);
    paramActivity.zzk(new ConnectionResult(i, null));
    return paramActivity.getTask();
  }
  
  public boolean showErrorDialogFragment(Activity paramActivity, int paramInt1, int paramInt2)
  {
    return showErrorDialogFragment(paramActivity, paramInt1, paramInt2, null);
  }
  
  public boolean showErrorDialogFragment(Activity paramActivity, int paramInt1, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    Dialog localDialog = getErrorDialog(paramActivity, paramInt1, paramInt2, paramOnCancelListener);
    if (localDialog == null) {
      return false;
    }
    zza(paramActivity, localDialog, "GooglePlayServicesErrorDialog", paramOnCancelListener);
    return true;
  }
  
  public void showErrorNotification(Context paramContext, int paramInt)
  {
    if (paramInt == 6) {
      Log.e("GoogleApiAvailability", "showErrorNotification(context, errorCode) is called for RESOLUTION_REQUIRED when showErrorNotification(context, result) should be called");
    }
    if (isUserResolvableError(paramInt)) {
      GooglePlayServicesUtil.showErrorNotification(paramInt, paramContext);
    }
  }
  
  public void showErrorNotification(Context paramContext, ConnectionResult paramConnectionResult)
  {
    PendingIntent localPendingIntent = getErrorResolutionPendingIntent(paramContext, paramConnectionResult);
    if (localPendingIntent != null) {
      GooglePlayServicesUtil.zza(paramConnectionResult.getErrorCode(), paramContext, localPendingIntent);
    }
  }
  
  public Dialog zza(Activity paramActivity, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    Object localObject = new ProgressBar(paramActivity, null, 16842874);
    ((ProgressBar)localObject).setIndeterminate(true);
    ((ProgressBar)localObject).setVisibility(0);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramActivity);
    localBuilder.setView((View)localObject);
    localBuilder.setMessage(com.google.android.gms.common.internal.zzi.zzi(paramActivity, 18));
    localBuilder.setTitle(com.google.android.gms.common.internal.zzi.zzg(paramActivity, 18));
    localBuilder.setPositiveButton("", null);
    localObject = localBuilder.create();
    zza(paramActivity, (Dialog)localObject, "GooglePlayServicesUpdatingDialog", paramOnCancelListener);
    return (Dialog)localObject;
  }
  
  @TargetApi(14)
  Dialog zza(Context paramContext, int paramInt, zzj paramzzj, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    Object localObject2 = null;
    if (paramInt == 0) {
      return null;
    }
    int i = paramInt;
    if (com.google.android.gms.common.util.zzi.zzcl(paramContext))
    {
      i = paramInt;
      if (paramInt == 2) {
        i = 42;
      }
    }
    Object localObject1 = localObject2;
    if (zzs.zzaxn())
    {
      TypedValue localTypedValue = new TypedValue();
      paramContext.getTheme().resolveAttribute(16843529, localTypedValue, true);
      localObject1 = localObject2;
      if ("Theme.Dialog.Alert".equals(paramContext.getResources().getResourceEntryName(localTypedValue.resourceId))) {
        localObject1 = new AlertDialog.Builder(paramContext, 5);
      }
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = new AlertDialog.Builder(paramContext);
    }
    ((AlertDialog.Builder)localObject2).setMessage(com.google.android.gms.common.internal.zzi.zzi(paramContext, i));
    if (paramOnCancelListener != null) {
      ((AlertDialog.Builder)localObject2).setOnCancelListener(paramOnCancelListener);
    }
    paramOnCancelListener = com.google.android.gms.common.internal.zzi.zzk(paramContext, i);
    if (paramOnCancelListener != null) {
      ((AlertDialog.Builder)localObject2).setPositiveButton(paramOnCancelListener, paramzzj);
    }
    paramContext = com.google.android.gms.common.internal.zzi.zzg(paramContext, i);
    if (paramContext != null) {
      ((AlertDialog.Builder)localObject2).setTitle(paramContext);
    }
    return ((AlertDialog.Builder)localObject2).create();
  }
  
  @Nullable
  public PendingIntent zza(Context paramContext, int paramInt1, int paramInt2, @Nullable String paramString)
  {
    return super.zza(paramContext, paramInt1, paramInt2, paramString);
  }
  
  @Nullable
  public Intent zza(Context paramContext, int paramInt, @Nullable String paramString)
  {
    return super.zza(paramContext, paramInt, paramString);
  }
  
  @Nullable
  public zzqv zza(Context paramContext, zzqv.zza paramzza)
  {
    Object localObject = new IntentFilter("android.intent.action.PACKAGE_ADDED");
    ((IntentFilter)localObject).addDataScheme("package");
    zzqv localzzqv = new zzqv(paramzza);
    paramContext.registerReceiver(localzzqv, (IntentFilter)localObject);
    localzzqv.setContext(paramContext);
    localObject = localzzqv;
    if (!zzs(paramContext, "com.google.android.gms"))
    {
      paramzza.zzaqp();
      localzzqv.unregister();
      localObject = null;
    }
    return (zzqv)localObject;
  }
  
  @TargetApi(11)
  void zza(Activity paramActivity, Dialog paramDialog, String paramString, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    try
    {
      bool = paramActivity instanceof FragmentActivity;
      if (bool)
      {
        paramActivity = ((FragmentActivity)paramActivity).getSupportFragmentManager();
        SupportErrorDialogFragment.newInstance(paramDialog, paramOnCancelListener).show(paramActivity, paramString);
        return;
      }
    }
    catch (NoClassDefFoundError localNoClassDefFoundError)
    {
      for (;;)
      {
        boolean bool = false;
      }
      if (zzs.zzaxk())
      {
        paramActivity = paramActivity.getFragmentManager();
        ErrorDialogFragment.newInstance(paramDialog, paramOnCancelListener).show(paramActivity, paramString);
        return;
      }
      throw new RuntimeException("This Activity does not support Fragments.");
    }
  }
  
  public void zza(Context paramContext, ConnectionResult paramConnectionResult, int paramInt)
  {
    PendingIntent localPendingIntent = getErrorResolutionPendingIntent(paramContext, paramConnectionResult);
    if (localPendingIntent != null) {
      GooglePlayServicesUtil.zza(paramConnectionResult.getErrorCode(), paramContext, GoogleApiActivity.zza(paramContext, localPendingIntent, paramInt));
    }
  }
  
  public boolean zza(Activity paramActivity, @NonNull zzrb paramzzrb, int paramInt1, int paramInt2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    paramzzrb = zza(paramActivity, paramInt1, zzj.zza(paramzzrb, zza(paramActivity, paramInt1, "d"), paramInt2), paramOnCancelListener);
    if (paramzzrb == null) {
      return false;
    }
    zza(paramActivity, paramzzrb, "GooglePlayServicesErrorDialog", paramOnCancelListener);
    return true;
  }
  
  public int zzbo(Context paramContext)
  {
    return super.zzbo(paramContext);
  }
  
  public boolean zzd(Context paramContext, int paramInt)
  {
    return super.zzd(paramContext, paramInt);
  }
  
  @Deprecated
  @Nullable
  public Intent zzfl(int paramInt)
  {
    return super.zzfl(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GoogleApiAvailability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */