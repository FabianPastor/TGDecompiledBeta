package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.gms.R.string;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzah;
import com.google.android.gms.common.internal.zzy;

@Deprecated
public final class zzrk
{
  private static zzrk Ba;
  private static final Object zzaox = new Object();
  private final Status Bb;
  private final boolean Bc;
  private final boolean Bd;
  private final String zzctj;
  
  zzrk(Context paramContext)
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
    for (this.Bd = bool2;; this.Bd = false)
    {
      this.Bc = bool1;
      String str = zzy.zzcd(paramContext);
      localObject = str;
      if (str == null) {
        localObject = new zzah(paramContext).getString("google_app_id");
      }
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        break label141;
      }
      this.Bb = new Status(10, "Missing google app id value from from string resources with name google_app_id.");
      this.zzctj = null;
      return;
      bool1 = false;
      break;
      label127:
      bool2 = false;
      break label52;
    }
    label141:
    this.zzctj = ((String)localObject);
    this.Bb = Status.xZ;
  }
  
  public static String zzatt()
  {
    return zzhh("getGoogleAppId").zzctj;
  }
  
  public static boolean zzatu()
  {
    return zzhh("isMeasurementExplicitlyDisabled").Bd;
  }
  
  public static Status zzby(Context paramContext)
  {
    zzaa.zzb(paramContext, "Context must not be null.");
    synchronized (zzaox)
    {
      if (Ba == null) {
        Ba = new zzrk(paramContext);
      }
      paramContext = Ba.Bb;
      return paramContext;
    }
  }
  
  private static zzrk zzhh(String paramString)
  {
    synchronized (zzaox)
    {
      if (Ba == null) {
        throw new IllegalStateException(String.valueOf(paramString).length() + 34 + "Initialize must be called before " + paramString + ".");
      }
    }
    paramString = Ba;
    return paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */