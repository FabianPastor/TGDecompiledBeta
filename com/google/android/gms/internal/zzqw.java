package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.gms.R.string;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzaj;

@Deprecated
public final class zzqw
{
  private static zzqw yP;
  private static Object zzaok = new Object();
  private final String yQ;
  private final Status yR;
  private final String yS;
  private final String yT;
  private final String yU;
  private final boolean yV;
  private final boolean yW;
  private final String zzcpe;
  
  zzqw(Context paramContext)
  {
    Object localObject = paramContext.getResources();
    int i = ((Resources)localObject).getIdentifier("google_app_measurement_enable", "integer", ((Resources)localObject).getResourcePackageName(R.string.common_google_play_services_unknown_issue));
    if (i != 0) {
      if (((Resources)localObject).getInteger(i) != 0)
      {
        bool1 = true;
        if (bool1) {
          break label172;
        }
      }
    }
    label52:
    for (this.yW = bool2;; this.yW = false)
    {
      this.yV = bool1;
      zzaj localzzaj = new zzaj(paramContext);
      this.yS = localzzaj.getString("firebase_database_url");
      this.yU = localzzaj.getString("google_storage_bucket");
      this.yT = localzzaj.getString("gcm_defaultSenderId");
      this.yQ = localzzaj.getString("google_api_key");
      localObject = zzaa.zzcg(paramContext);
      paramContext = (Context)localObject;
      if (localObject == null) {
        paramContext = localzzaj.getString("google_app_id");
      }
      if (!TextUtils.isEmpty(paramContext)) {
        break label186;
      }
      this.yR = new Status(10, "Missing google app id value from from string resources with name google_app_id.");
      this.zzcpe = null;
      return;
      bool1 = false;
      break;
      label172:
      bool2 = false;
      break label52;
    }
    label186:
    this.zzcpe = paramContext;
    this.yR = Status.vY;
  }
  
  zzqw(String paramString, boolean paramBoolean)
  {
    this(paramString, paramBoolean, null, null, null);
  }
  
  zzqw(String paramString1, boolean paramBoolean, String paramString2, String paramString3, String paramString4)
  {
    this.zzcpe = paramString1;
    this.yQ = null;
    this.yR = Status.vY;
    this.yV = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.yW = paramBoolean;
      this.yS = paramString2;
      this.yT = paramString4;
      this.yU = paramString3;
      return;
    }
  }
  
  public static String zzasl()
  {
    return zzhf("getGoogleAppId").zzcpe;
  }
  
  public static boolean zzasm()
  {
    return zzhf("isMeasurementExplicitlyDisabled").yW;
  }
  
  public static Status zzb(Context arg0, String paramString, boolean paramBoolean)
  {
    zzac.zzb(???, "Context must not be null.");
    zzac.zzh(paramString, "App ID must be nonempty.");
    synchronized (zzaok)
    {
      if (yP != null)
      {
        paramString = yP.zzhe(paramString);
        return paramString;
      }
      yP = new zzqw(paramString, paramBoolean);
      paramString = yP.yR;
      return paramString;
    }
  }
  
  public static Status zzcb(Context paramContext)
  {
    zzac.zzb(paramContext, "Context must not be null.");
    synchronized (zzaok)
    {
      if (yP == null) {
        yP = new zzqw(paramContext);
      }
      paramContext = yP.yR;
      return paramContext;
    }
  }
  
  private static zzqw zzhf(String paramString)
  {
    synchronized (zzaok)
    {
      if (yP == null) {
        throw new IllegalStateException(String.valueOf(paramString).length() + 34 + "Initialize must be called before " + paramString + ".");
      }
    }
    paramString = yP;
    return paramString;
  }
  
  Status zzhe(String paramString)
  {
    if ((this.zzcpe != null) && (!this.zzcpe.equals(paramString)))
    {
      paramString = this.zzcpe;
      return new Status(10, String.valueOf(paramString).length() + 97 + "Initialize was called with two different Google App IDs.  Only the first app ID will be used: '" + paramString + "'.");
    }
    return Status.vY;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */