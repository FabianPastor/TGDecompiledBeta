package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzrk;

public class zzn
  extends zzaa
{
  private String aqF;
  private String aqM;
  private int asv;
  private long asw;
  private String bN;
  private String bO;
  private String zzctj;
  
  zzn(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  String zzaaf()
  {
    zzacj();
    return this.bO;
  }
  
  String zzbth()
  {
    zzacj();
    return this.aqF;
  }
  
  String zzbtn()
  {
    zzacj();
    return this.aqM;
  }
  
  long zzbto()
  {
    return zzbwd().zzbto();
  }
  
  @WorkerThread
  long zzbtp()
  {
    zzacj();
    zzzx();
    if (this.asw == 0L) {
      this.asw = this.aqw.zzbvx().zzad(getContext(), getContext().getPackageName());
    }
    return this.asw;
  }
  
  int zzbwx()
  {
    zzacj();
    return this.asv;
  }
  
  protected void zzdw(Status paramStatus)
  {
    if (paramStatus == null)
    {
      zzbwb().zzbwy().log("GoogleService failed to initialize (no status)");
      return;
    }
    zzbwb().zzbwy().zze("GoogleService failed to initialize, status", Integer.valueOf(paramStatus.getStatusCode()), paramStatus.getStatusMessage());
  }
  
  @WorkerThread
  AppMetadata zzmi(String paramString)
  {
    zzzx();
    String str1 = zzup();
    String str2 = zzbth();
    String str3 = zzaaf();
    long l1 = zzbwx();
    String str4 = zzbtn();
    long l2 = zzbto();
    long l3 = zzbtp();
    boolean bool2 = this.aqw.isEnabled();
    if (!zzbwc().atn) {}
    for (boolean bool1 = true;; bool1 = false) {
      return new AppMetadata(str1, str2, str3, l1, str4, l2, l3, paramString, bool2, bool1, zzbwc().zzbtj());
    }
  }
  
  String zzup()
  {
    zzacj();
    return this.zzctj;
  }
  
  protected void zzzy()
  {
    Object localObject3 = "unknown";
    String str3 = "Unknown";
    int j = Integer.MIN_VALUE;
    String str1 = "Unknown";
    String str4 = getContext().getPackageName();
    Object localObject6 = getContext().getPackageManager();
    Object localObject5;
    String str2;
    int i;
    Object localObject1;
    if (localObject6 == null)
    {
      zzbwb().zzbwy().log("PackageManager is null, app identity information might be inaccurate");
      localObject5 = localObject3;
      str2 = str3;
      i = j;
      localObject3 = str1;
      this.zzctj = str4;
      this.aqM = ((String)localObject5);
      this.bO = str2;
      this.asv = i;
      this.bN = ((String)localObject3);
      zzbwd().zzayi();
      localObject1 = zzrk.zzby(getContext());
      if ((localObject1 == null) || (!((Status)localObject1).isSuccess())) {
        break label462;
      }
      i = 1;
      label122:
      if (i == 0) {
        zzdw((Status)localObject1);
      }
      if (i == 0) {
        break label562;
      }
      localObject1 = zzbwd().zzbuu();
      if (!zzbwd().zzbut()) {
        break label467;
      }
      zzbwb().zzbxc().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      i = 0;
    }
    for (;;)
    {
      this.aqF = "";
      if (!zzbwd().zzayi()) {}
      try
      {
        localObject3 = zzrk.zzatt();
        localObject1 = localObject3;
        if (TextUtils.isEmpty((CharSequence)localObject3)) {
          localObject1 = "";
        }
        this.aqF = ((String)localObject1);
        if (i != 0) {
          zzbwb().zzbxe().zze("App package, google app id", this.zzctj, this.aqF);
        }
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        label247:
        label256:
        PackageInfo localPackageInfo;
        Object localObject2;
        Object localObject4;
        zzbwb().zzbwy().zzj("getGoogleAppId or isMeasurementEnabled failed with exception", localIllegalStateException);
        return;
      }
      try
      {
        localObject1 = ((PackageManager)localObject6).getInstallerPackageName(str4);
        localObject3 = localObject1;
        if (localObject3 == null)
        {
          localObject1 = "manual_install";
          localObject5 = str1;
          str2 = str3;
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        try
        {
          localPackageInfo = ((PackageManager)localObject6).getPackageInfo(getContext().getPackageName(), 0);
          localObject3 = str1;
          i = j;
          str2 = str3;
          localObject5 = localObject1;
          if (localPackageInfo == null) {
            break;
          }
          localObject5 = str1;
          str2 = str3;
          localObject6 = ((PackageManager)localObject6).getApplicationLabel(localPackageInfo.applicationInfo);
          localObject3 = str1;
          localObject5 = str1;
          str2 = str3;
          if (!TextUtils.isEmpty((CharSequence)localObject6))
          {
            localObject5 = str1;
            str2 = str3;
            localObject3 = ((CharSequence)localObject6).toString();
          }
          localObject5 = localObject3;
          str2 = str3;
          str1 = localPackageInfo.versionName;
          localObject5 = localObject3;
          str2 = str1;
          i = localPackageInfo.versionCode;
          str2 = str1;
          localObject5 = localObject1;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          zzbwb().zzbwy().zzj("Error retrieving package info: appName", localObject5);
          localObject4 = localObject5;
          i = j;
          localObject5 = localObject2;
        }
        localIllegalArgumentException = localIllegalArgumentException;
        zzbwb().zzbwy().zzj("Error retrieving app installer package name", str4);
        break label247;
        localObject2 = localObject3;
        if (!"com.android.vending".equals(localObject3)) {
          break label256;
        }
        localObject2 = "";
        break label256;
      }
      break;
      label462:
      i = 0;
      break label122;
      label467:
      if ((localObject2 != null) && (!((Boolean)localObject2).booleanValue()))
      {
        zzbwb().zzbxc().log("Collection disabled with firebase_analytics_collection_enabled=0");
        i = 0;
      }
      else if ((localObject2 == null) && (zzbwd().zzatu()))
      {
        zzbwb().zzbxc().log("Collection disabled with google_app_measurement_enable=0");
        i = 0;
      }
      else
      {
        zzbwb().zzbxe().log("Collection enabled");
        i = 1;
        continue;
        label562:
        i = 0;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */