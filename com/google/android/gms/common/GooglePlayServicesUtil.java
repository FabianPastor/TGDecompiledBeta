package com.google.android.gms.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import com.google.android.gms.common.internal.DialogRedirect;

public final class GooglePlayServicesUtil
  extends GooglePlayServicesUtilLight
{
  @Deprecated
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = GooglePlayServicesUtilLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  
  public static Context getRemoteContext(Context paramContext)
  {
    return GooglePlayServicesUtilLight.getRemoteContext(paramContext);
  }
  
  public static Resources getRemoteResource(Context paramContext)
  {
    return GooglePlayServicesUtilLight.getRemoteResource(paramContext);
  }
  
  @Deprecated
  public static int isGooglePlayServicesAvailable(Context paramContext)
  {
    return GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(paramContext);
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
    if (GooglePlayServicesUtilLight.isPlayServicesPossiblyUpdating(paramActivity, paramInt1)) {
      i = 18;
    }
    GoogleApiAvailability localGoogleApiAvailability = GoogleApiAvailability.getInstance();
    boolean bool;
    if (paramFragment == null) {
      bool = localGoogleApiAvailability.showErrorDialogFragment(paramActivity, i, paramInt2, paramOnCancelListener);
    }
    for (;;)
    {
      return bool;
      paramFragment = GoogleApiAvailability.zza(paramActivity, i, DialogRedirect.getInstance(paramFragment, GoogleApiAvailability.getInstance().getErrorResolutionIntent(paramActivity, i, "d"), paramInt2), paramOnCancelListener);
      if (paramFragment == null)
      {
        bool = false;
      }
      else
      {
        GoogleApiAvailability.zza(paramActivity, paramFragment, "GooglePlayServicesErrorDialog", paramOnCancelListener);
        bool = true;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GooglePlayServicesUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */