package com.google.android.gms.internal.measurement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.util.Clock;

@TargetApi(14)
final class zzif
  implements Application.ActivityLifecycleCallbacks
{
  private zzif(zzhm paramzzhm) {}
  
  public final void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    label112:
    int i;
    try
    {
      this.zzaop.zzgg().zzir().log("onActivityCreated");
      Object localObject = paramActivity.getIntent();
      if (localObject != null)
      {
        Uri localUri = ((Intent)localObject).getData();
        if ((localUri != null) && (localUri.isHierarchical()))
        {
          if (paramBundle == null)
          {
            Bundle localBundle = this.zzaop.zzgc().zza(localUri);
            this.zzaop.zzgc();
            if (!zzjv.zzd((Intent)localObject)) {
              break label112;
            }
            localObject = "gs";
            if (localBundle != null) {
              this.zzaop.zza((String)localObject, "_cmp", localBundle);
            }
          }
          localObject = localUri.getQueryParameter("referrer");
          if (TextUtils.isEmpty((CharSequence)localObject)) {}
          for (;;)
          {
            return;
            localObject = "auto";
            break;
            if ((!((String)localObject).contains("gclid")) || ((!((String)localObject).contains("utm_campaign")) && (!((String)localObject).contains("utm_source")) && (!((String)localObject).contains("utm_medium")) && (!((String)localObject).contains("utm_term")) && (!((String)localObject).contains("utm_content")))) {
              break label277;
            }
            i = 1;
            label175:
            if (i != 0) {
              break label283;
            }
            this.zzaop.zzgg().zziq().log("Activity created with data 'referrer' param without gclid and at least one utm field");
          }
        }
      }
      localzzih = this.zzaop.zzfy();
    }
    catch (Throwable localThrowable)
    {
      this.zzaop.zzgg().zzil().zzg("Throwable caught in onActivityCreated", localThrowable);
    }
    zzih localzzih;
    while (paramBundle != null)
    {
      paramBundle = paramBundle.getBundle("com.google.firebase.analytics.screen_service");
      if (paramBundle == null) {
        break;
      }
      paramActivity = localzzih.zze(paramActivity);
      paramActivity.zzapb = paramBundle.getLong("id");
      paramActivity.zzug = paramBundle.getString("name");
      paramActivity.zzapa = paramBundle.getString("referrer_name");
      break;
      label277:
      i = 0;
      break label175;
      label283:
      this.zzaop.zzgg().zziq().zzg("Activity created with referrer", localzzih);
      if (!TextUtils.isEmpty(localzzih)) {
        this.zzaop.zza("auto", "_ldl", localzzih);
      }
    }
  }
  
  public final void onActivityDestroyed(Activity paramActivity)
  {
    this.zzaop.zzfy().onActivityDestroyed(paramActivity);
  }
  
  public final void onActivityPaused(Activity paramActivity)
  {
    this.zzaop.zzfy().onActivityPaused(paramActivity);
    paramActivity = this.zzaop.zzge();
    long l = paramActivity.zzbt().elapsedRealtime();
    paramActivity.zzgf().zzc(new zzjo(paramActivity, l));
  }
  
  public final void onActivityResumed(Activity paramActivity)
  {
    this.zzaop.zzfy().onActivityResumed(paramActivity);
    paramActivity = this.zzaop.zzge();
    long l = paramActivity.zzbt().elapsedRealtime();
    paramActivity.zzgf().zzc(new zzjn(paramActivity, l));
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    this.zzaop.zzfy().onActivitySaveInstanceState(paramActivity, paramBundle);
  }
  
  public final void onActivityStarted(Activity paramActivity) {}
  
  public final void onActivityStopped(Activity paramActivity) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzif.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */