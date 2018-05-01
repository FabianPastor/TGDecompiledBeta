package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.util.zzd;

@TargetApi(14)
final class zzckb
  implements Application.ActivityLifecycleCallbacks
{
  private zzckb(zzcjn paramzzcjn) {}
  
  public final void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    for (;;)
    {
      try
      {
        this.zzjhc.zzawy().zzazj().log("onActivityCreated");
        Object localObject1 = paramActivity.getIntent();
        if (localObject1 == null) {
          continue;
        }
        Uri localUri = ((Intent)localObject1).getData();
        if ((localUri == null) || (!localUri.isHierarchical())) {
          continue;
        }
        if (paramBundle == null)
        {
          Bundle localBundle = this.zzjhc.zzawu().zzp(localUri);
          this.zzjhc.zzawu();
          if (!zzclq.zzo((Intent)localObject1)) {
            break label331;
          }
          localObject1 = "gs";
          if (localBundle != null) {
            this.zzjhc.zzc((String)localObject1, "_cmp", localBundle);
          }
        }
        localObject1 = localUri.getQueryParameter("referrer");
        if (TextUtils.isEmpty((CharSequence)localObject1)) {
          return;
        }
        if (!((String)localObject1).contains("gclid")) {
          continue;
        }
        if ((((String)localObject1).contains("utm_campaign")) || (((String)localObject1).contains("utm_source")) || (((String)localObject1).contains("utm_medium")) || (((String)localObject1).contains("utm_term"))) {
          break label338;
        }
        if (!((String)localObject1).contains("utm_content")) {
          continue;
        }
      }
      catch (Throwable localThrowable)
      {
        this.zzjhc.zzawy().zzazd().zzj("Throwable caught in onActivityCreated", localThrowable);
        localObject2 = this.zzjhc.zzawq();
        if (paramBundle == null) {
          continue;
        }
        paramBundle = paramBundle.getBundle("com.google.firebase.analytics.screen_service");
        if (paramBundle == null) {
          continue;
        }
        paramActivity = ((zzckc)localObject2).zzq(paramActivity);
        paramActivity.zziwm = paramBundle.getLong("id");
        paramActivity.zziwk = paramBundle.getString("name");
        paramActivity.zziwl = paramBundle.getString("referrer_name");
        return;
        i = 0;
        continue;
        this.zzjhc.zzawy().zzazi().zzj("Activity created with referrer", localObject2);
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          continue;
        }
        this.zzjhc.zzb("auto", "_ldl", localObject2);
        continue;
        return;
      }
      if (i == 0)
      {
        this.zzjhc.zzawy().zzazi().log("Activity created with data 'referrer' param without gclid and at least one utm field");
        return;
      }
      label331:
      Object localObject2 = "auto";
      continue;
      label338:
      int i = 1;
    }
  }
  
  public final void onActivityDestroyed(Activity paramActivity)
  {
    this.zzjhc.zzawq().onActivityDestroyed(paramActivity);
  }
  
  public final void onActivityPaused(Activity paramActivity)
  {
    this.zzjhc.zzawq().onActivityPaused(paramActivity);
    paramActivity = this.zzjhc.zzaww();
    long l = paramActivity.zzws().elapsedRealtime();
    paramActivity.zzawx().zzg(new zzclj(paramActivity, l));
  }
  
  public final void onActivityResumed(Activity paramActivity)
  {
    this.zzjhc.zzawq().onActivityResumed(paramActivity);
    paramActivity = this.zzjhc.zzaww();
    long l = paramActivity.zzws().elapsedRealtime();
    paramActivity.zzawx().zzg(new zzcli(paramActivity, l));
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    this.zzjhc.zzawq().onActivitySaveInstanceState(paramActivity, paramBundle);
  }
  
  public final void onActivityStarted(Activity paramActivity) {}
  
  public final void onActivityStopped(Activity paramActivity) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */