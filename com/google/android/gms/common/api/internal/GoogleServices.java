package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.gms.common.R.string;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.MetadataValueReader;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.StringResourceValueReader;
import javax.annotation.concurrent.GuardedBy;

@Deprecated
public final class GoogleServices
{
  private static final Object sLock = new Object();
  @GuardedBy("sLock")
  private static GoogleServices zzku;
  private final String zzkv;
  private final Status zzkw;
  private final boolean zzkx;
  private final boolean zzky;
  
  GoogleServices(Context paramContext)
  {
    Object localObject = paramContext.getResources();
    int i = ((Resources)localObject).getIdentifier("google_app_measurement_enable", "integer", ((Resources)localObject).getResourcePackageName(R.string.common_google_play_services_unknown_issue));
    if (i != 0) {
      if (((Resources)localObject).getInteger(i) != 0)
      {
        bool1 = true;
        if (bool1) {
          break label128;
        }
        label54:
        this.zzky = bool2;
        label59:
        this.zzkx = bool1;
        String str = MetadataValueReader.getGoogleAppId(paramContext);
        localObject = str;
        if (str == null) {
          localObject = new StringResourceValueReader(paramContext).getString("google_app_id");
        }
        if (!TextUtils.isEmpty((CharSequence)localObject)) {
          break label141;
        }
        this.zzkw = new Status(10, "Missing google app id value from from string resources with name google_app_id.");
        this.zzkv = null;
      }
    }
    for (;;)
    {
      return;
      bool1 = false;
      break;
      label128:
      bool2 = false;
      break label54;
      this.zzky = false;
      break label59;
      label141:
      this.zzkv = ((String)localObject);
      this.zzkw = Status.RESULT_SUCCESS;
    }
  }
  
  private static GoogleServices checkInitialized(String paramString)
  {
    synchronized (sLock)
    {
      if (zzku == null)
      {
        IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
        int i = String.valueOf(paramString).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 34);
        localIllegalStateException.<init>("Initialize must be called before " + paramString + ".");
        throw localIllegalStateException;
      }
    }
    paramString = zzku;
    return paramString;
  }
  
  public static String getGoogleAppId()
  {
    return checkInitialized("getGoogleAppId").zzkv;
  }
  
  public static Status initialize(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext, "Context must not be null.");
    synchronized (sLock)
    {
      if (zzku == null)
      {
        GoogleServices localGoogleServices = new com/google/android/gms/common/api/internal/GoogleServices;
        localGoogleServices.<init>(paramContext);
        zzku = localGoogleServices;
      }
      paramContext = zzku.zzkw;
      return paramContext;
    }
  }
  
  public static boolean isMeasurementExplicitlyDisabled()
  {
    return checkInitialized("isMeasurementExplicitlyDisabled").zzky;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/GoogleServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */