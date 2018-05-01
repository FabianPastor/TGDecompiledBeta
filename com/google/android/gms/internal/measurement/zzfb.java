package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.GoogleServices;
import com.google.android.gms.common.wrappers.InstantApps;
import com.google.firebase.iid.FirebaseInstanceId;

public final class zzfb
  extends zzhk
{
  private String zzadh;
  private String zzado;
  private long zzads;
  private int zzaei;
  private int zzaie;
  private long zzaif;
  private String zztb;
  private String zztc;
  private String zztd;
  
  zzfb(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final String zzgl()
  {
    Object localObject = null;
    zzab();
    if ((zzgi().zzd(this.zztd, zzew.zzaib)) && (!this.zzacr.isEnabled())) {}
    for (;;)
    {
      return (String)localObject;
      try
      {
        String str = FirebaseInstanceId.getInstance().getId();
        localObject = str;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        zzgg().zzin().log("Failed to retrieve Firebase Instance Id");
      }
    }
  }
  
  final String getGmpAppId()
  {
    zzch();
    return this.zzadh;
  }
  
  final String zzah()
  {
    zzch();
    return this.zztd;
  }
  
  final zzec zzbd(String paramString)
  {
    zzab();
    String str1 = zzah();
    String str2 = getGmpAppId();
    zzch();
    String str3 = this.zztc;
    long l1 = zzii();
    zzch();
    String str4 = this.zzado;
    zzch();
    zzab();
    if (this.zzaif == 0L) {
      this.zzaif = this.zzacr.zzgc().zzd(getContext(), getContext().getPackageName());
    }
    long l2 = this.zzaif;
    boolean bool1 = this.zzacr.isEnabled();
    boolean bool2;
    String str5;
    long l3;
    int i;
    label163:
    boolean bool4;
    if (!zzgh().zzakm)
    {
      bool2 = true;
      str5 = zzgl();
      zzch();
      l3 = this.zzacr.zzju();
      i = zzij();
      Boolean localBoolean = zzgi().zzas("google_analytics_adid_collection_enabled");
      if ((localBoolean != null) && (!localBoolean.booleanValue())) {
        break label256;
      }
      bool3 = true;
      bool4 = Boolean.valueOf(bool3).booleanValue();
      localBoolean = zzgi().zzas("google_analytics_ssaid_collection_enabled");
      if ((localBoolean != null) && (!localBoolean.booleanValue())) {
        break label262;
      }
    }
    label256:
    label262:
    for (boolean bool3 = true;; bool3 = false)
    {
      return new zzec(str1, str2, str3, l1, str4, 12451L, l2, paramString, bool1, bool2, str5, 0L, l3, i, bool4, Boolean.valueOf(bool3).booleanValue(), zzgh().zzja());
      bool2 = false;
      break;
      bool3 = false;
      break label163;
    }
  }
  
  protected final boolean zzhh()
  {
    return true;
  }
  
  protected final void zzig()
  {
    int i = 1;
    Object localObject1 = "unknown";
    String str1 = "Unknown";
    int j = Integer.MIN_VALUE;
    String str2 = "Unknown";
    String str3 = getContext().getPackageName();
    Object localObject3 = getContext().getPackageManager();
    Object localObject4;
    String str4;
    int k;
    Object localObject5;
    if (localObject3 == null)
    {
      zzgg().zzil().zzg("PackageManager is null, app identity information might be inaccurate. appId", zzfg.zzbh(str3));
      localObject4 = localObject1;
      str4 = str1;
      k = j;
      localObject1 = str2;
      this.zztd = str3;
      this.zzado = ((String)localObject4);
      this.zztc = str4;
      this.zzaie = k;
      this.zztb = ((String)localObject1);
      this.zzaif = 0L;
      localObject5 = GoogleServices.initialize(getContext());
      if ((localObject5 == null) || (!((Status)localObject5).isSuccess())) {
        break label517;
      }
      k = 1;
      label129:
      if (k == 0)
      {
        if (localObject5 != null) {
          break label523;
        }
        zzgg().zzil().log("GoogleService failed to initialize (no status)");
      }
      label152:
      if (k == 0) {
        break label672;
      }
      localObject5 = zzgi().zzas("firebase_analytics_collection_enabled");
      if (!zzgi().zzhi()) {
        break label552;
      }
      zzgg().zzip().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      k = 0;
    }
    for (;;)
    {
      this.zzadh = "";
      this.zzads = 0L;
      try
      {
        localObject1 = GoogleServices.getGoogleAppId();
        localObject5 = localObject1;
        if (TextUtils.isEmpty((CharSequence)localObject1)) {
          localObject5 = "";
        }
        this.zzadh = ((String)localObject5);
        if (k != 0) {
          zzgg().zzir().zze("App package, google app id", this.zztd, this.zzadh);
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        for (;;)
        {
          Object localObject6;
          label517:
          label523:
          label552:
          zzgg().zzil().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzfg.zzbh(str3), localIllegalStateException);
          continue;
          k = 0;
          continue;
          this.zzaei = 0;
        }
      }
      if (Build.VERSION.SDK_INT >= 16) {
        if (InstantApps.isInstantApp(getContext()))
        {
          k = i;
          this.zzaei = k;
          return;
          try
          {
            localObject5 = ((PackageManager)localObject3).getInstallerPackageName(str3);
            localObject1 = localObject5;
            if (localObject1 == null)
            {
              localObject5 = "manual_install";
              localObject4 = str2;
              str4 = str1;
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            for (;;)
            {
              try
              {
                PackageInfo localPackageInfo = ((PackageManager)localObject3).getPackageInfo(getContext().getPackageName(), 0);
                localObject1 = str2;
                k = j;
                str4 = str1;
                localObject4 = localObject5;
                if (localPackageInfo == null) {
                  break;
                }
                localObject4 = str2;
                str4 = str1;
                localObject3 = ((PackageManager)localObject3).getApplicationLabel(localPackageInfo.applicationInfo);
                localObject1 = str2;
                localObject4 = str2;
                str4 = str1;
                if (!TextUtils.isEmpty((CharSequence)localObject3))
                {
                  localObject4 = str2;
                  str4 = str1;
                  localObject1 = ((CharSequence)localObject3).toString();
                }
                localObject4 = localObject1;
                str4 = str1;
                str2 = localPackageInfo.versionName;
                localObject4 = localObject1;
                str4 = str2;
                k = localPackageInfo.versionCode;
                str4 = str2;
                localObject4 = localObject5;
              }
              catch (PackageManager.NameNotFoundException localNameNotFoundException)
              {
                zzgg().zzil().zze("Error retrieving package info. appId, appName", zzfg.zzbh(str3), localObject4);
                Object localObject2 = localObject4;
                k = j;
                localObject4 = localObject6;
              }
              localIllegalArgumentException = localIllegalArgumentException;
              zzgg().zzil().zzg("Error retrieving app installer package name. appId", zzfg.zzbh(str3));
              continue;
              localObject6 = localObject1;
              if ("com.android.vending".equals(localObject1)) {
                localObject6 = "";
              }
            }
          }
          break;
          k = 0;
          break label129;
          zzgg().zzil().zze("GoogleService failed to initialize, status", Integer.valueOf(((Status)localObject6).getStatusCode()), ((Status)localObject6).getStatusMessage());
          break label152;
          if ((localObject6 != null) && (!((Boolean)localObject6).booleanValue()))
          {
            zzgg().zzip().log("Collection disabled with firebase_analytics_collection_enabled=0");
            k = 0;
            continue;
          }
          if ((localObject6 == null) && (GoogleServices.isMeasurementExplicitlyDisabled()))
          {
            zzgg().zzip().log("Collection disabled with google_app_measurement_enable=0");
            k = 0;
            continue;
          }
          zzgg().zzir().log("Collection enabled");
          k = 1;
          continue;
        }
      }
      label672:
      k = 0;
    }
  }
  
  final int zzii()
  {
    zzch();
    return this.zzaie;
  }
  
  final int zzij()
  {
    zzch();
    return this.zzaei;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */