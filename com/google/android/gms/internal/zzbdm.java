package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.gms.R.string;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzby;

@Deprecated
public final class zzbdm
{
  private static zzbdm zzaEB;
  private static final Object zzuF = new Object();
  private final String mAppId;
  private final Status zzaEC;
  private final boolean zzaED;
  private final boolean zzaEE;
  
  private zzbdm(Context paramContext)
  {
    Object localObject = paramContext.getResources();
    int i = ((Resources)localObject).getIdentifier("google_app_measurement_enable", "integer", ((Resources)localObject).getResourcePackageName(R.string.common_google_play_services_unknown_issue));
    if (i != 0) {
      if (((Resources)localObject).getInteger(i) != 0)
      {
        bool1 = true;
        if (bool1) {
          break label127;
        }
      }
    }
    label52:
    for (this.zzaEE = bool2;; this.zzaEE = false)
    {
      this.zzaED = bool1;
      String str = zzbd.zzaD(paramContext);
      localObject = str;
      if (str == null) {
        localObject = new zzby(paramContext).getString("google_app_id");
      }
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        break label141;
      }
      this.zzaEC = new Status(10, "Missing google app id value from from string resources with name google_app_id.");
      this.mAppId = null;
      return;
      bool1 = false;
      break;
      label127:
      bool2 = false;
      break label52;
    }
    label141:
    this.mAppId = ((String)localObject);
    this.zzaEC = Status.zzaBm;
  }
  
  public static Status zzaz(Context paramContext)
  {
    zzbo.zzb(paramContext, "Context must not be null.");
    synchronized (zzuF)
    {
      if (zzaEB == null) {
        zzaEB = new zzbdm(paramContext);
      }
      paramContext = zzaEB.zzaEC;
      return paramContext;
    }
  }
  
  private static zzbdm zzcu(String paramString)
  {
    synchronized (zzuF)
    {
      if (zzaEB == null) {
        throw new IllegalStateException(String.valueOf(paramString).length() + 34 + "Initialize must be called before " + paramString + ".");
      }
    }
    paramString = zzaEB;
    return paramString;
  }
  
  public static String zzqA()
  {
    return zzcu("getGoogleAppId").mAppId;
  }
  
  public static boolean zzqB()
  {
    return zzcu("isMeasurementExplicitlyDisabled").zzaEE;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */