package com.google.android.gms.wearable.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class zzhg
  extends GmsClient<zzep>
{
  private final ExecutorService zzew;
  private final zzer<Object> zzex = new zzer();
  private final zzer<Object> zzey = new zzer();
  private final zzer<ChannelApi.ChannelListener> zzez = new zzer();
  private final zzer<DataApi.DataListener> zzfa = new zzer();
  private final zzer<MessageApi.MessageListener> zzfb = new zzer();
  private final zzer<Object> zzfc = new zzer();
  private final zzer<Object> zzfd = new zzer();
  private final zzer<CapabilityApi.CapabilityListener> zzfe = new zzer();
  private final zzhp zzff;
  
  public zzhg(Context paramContext, Looper paramLooper, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, ClientSettings paramClientSettings)
  {
    this(paramContext, paramLooper, paramConnectionCallbacks, paramOnConnectionFailedListener, paramClientSettings, Executors.newCachedThreadPool(), zzhp.zza(paramContext));
  }
  
  private zzhg(Context paramContext, Looper paramLooper, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, ClientSettings paramClientSettings, ExecutorService paramExecutorService, zzhp paramzzhp)
  {
    super(paramContext, paramLooper, 14, paramClientSettings, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzew = ((ExecutorService)Preconditions.checkNotNull(paramExecutorService));
    this.zzff = paramzzhp;
  }
  
  public final void connect(BaseGmsClient.ConnectionProgressReportCallbacks paramConnectionProgressReportCallbacks)
  {
    int i = 0;
    if (!requiresGooglePlayServices()) {}
    for (;;)
    {
      try
      {
        localObject = getContext().getPackageManager().getApplicationInfo("com.google.android.wearable.app.cn", 128).metaData;
        if (localObject != null) {
          i = ((Bundle)localObject).getInt("com.google.android.wearable.api.version", 0);
        }
        if (i >= GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE) {
          break label193;
        }
        int j = GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        localObject = new java/lang/StringBuilder;
        ((StringBuilder)localObject).<init>(86);
        Log.w("WearableClient", "The Wear OS app is out of date. Requires API version " + j + " but found " + i);
        Context localContext1 = getContext();
        Context localContext2 = getContext();
        localObject = new android/content/Intent;
        ((Intent)localObject).<init>("com.google.android.wearable.app.cn.UPDATE_ANDROID_WEAR");
        localObject = ((Intent)localObject).setPackage("com.google.android.wearable.app.cn");
        if (localContext2.getPackageManager().resolveActivity((Intent)localObject, 65536) != null)
        {
          triggerNotAvailable(paramConnectionProgressReportCallbacks, 6, PendingIntent.getActivity(localContext1, 0, (Intent)localObject, 0));
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Object localObject;
        triggerNotAvailable(paramConnectionProgressReportCallbacks, 16, null);
        continue;
      }
      localObject = new Intent("android.intent.action.VIEW", Uri.parse("market://details").buildUpon().appendQueryParameter("id", "com.google.android.wearable.app.cn").build());
      continue;
      label193:
      super.connect(paramConnectionProgressReportCallbacks);
    }
  }
  
  public final int getMinApkVersion()
  {
    return 12451000;
  }
  
  protected final String getServiceDescriptor()
  {
    return "com.google.android.gms.wearable.internal.IWearableService";
  }
  
  protected final String getStartServiceAction()
  {
    return "com.google.android.gms.wearable.BIND";
  }
  
  protected final String getStartServicePackage()
  {
    if (this.zzff.zze("com.google.android.wearable.app.cn")) {}
    for (String str = "com.google.android.wearable.app.cn";; str = "com.google.android.gms") {
      return str;
    }
  }
  
  protected final void onPostInitHandler(int paramInt1, IBinder paramIBinder, Bundle paramBundle, int paramInt2)
  {
    if (Log.isLoggable("WearableClient", 2)) {
      Log.v("WearableClient", 41 + "onPostInitHandler: statusCode " + paramInt1);
    }
    if (paramInt1 == 0)
    {
      this.zzex.zza(paramIBinder);
      this.zzey.zza(paramIBinder);
      this.zzez.zza(paramIBinder);
      this.zzfa.zza(paramIBinder);
      this.zzfb.zza(paramIBinder);
      this.zzfc.zza(paramIBinder);
      this.zzfd.zza(paramIBinder);
      this.zzfe.zza(paramIBinder);
    }
    super.onPostInitHandler(paramInt1, paramIBinder, paramBundle, paramInt2);
  }
  
  public final boolean requiresGooglePlayServices()
  {
    if (!this.zzff.zze("com.google.android.wearable.app.cn")) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzhg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */