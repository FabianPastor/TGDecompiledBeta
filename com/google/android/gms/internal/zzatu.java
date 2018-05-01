package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;

public class zzatu
  extends zzauh
{
  private String mAppId;
  private String zzVX;
  private String zzacL;
  private String zzacM;
  private String zzbqv;
  private long zzbqz;
  private int zzbsw;
  private long zzbsx;
  private int zzbsy;
  
  zzatu(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  String getGmpAppId()
  {
    zzob();
    return this.zzVX;
  }
  
  String zzKu()
  {
    zzob();
    return this.zzbqv;
  }
  
  long zzKv()
  {
    return zzKn().zzKv();
  }
  
  @WorkerThread
  long zzKw()
  {
    zzob();
    zzmR();
    if (this.zzbsx == 0L) {
      this.zzbsx = this.zzbqb.zzKh().zzM(getContext(), getContext().getPackageName());
    }
    return this.zzbsx;
  }
  
  int zzLX()
  {
    zzob();
    return this.zzbsw;
  }
  
  int zzLY()
  {
    zzob();
    return this.zzbsy;
  }
  
  protected void zzbw(Status paramStatus)
  {
    if (paramStatus == null)
    {
      zzKl().zzLZ().log("GoogleService failed to initialize (no status)");
      return;
    }
    zzKl().zzLZ().zze("GoogleService failed to initialize, status", Integer.valueOf(paramStatus.getStatusCode()), paramStatus.getStatusMessage());
  }
  
  @WorkerThread
  zzatd zzfD(String paramString)
  {
    zzmR();
    String str1 = zzke();
    String str2 = getGmpAppId();
    String str3 = zzmZ();
    long l1 = zzLX();
    String str4 = zzKu();
    long l2 = zzKv();
    long l3 = zzKw();
    boolean bool2 = this.zzbqb.isEnabled();
    if (!zzKm().zzbts) {}
    for (boolean bool1 = true;; bool1 = false) {
      return new zzatd(str1, str2, str3, l1, str4, l2, l3, paramString, bool2, bool1, zzKm().zzKq(), zzuW(), this.zzbqb.zzMF(), zzLY());
    }
  }
  
  String zzke()
  {
    zzob();
    return this.mAppId;
  }
  
  protected void zzmS()
  {
    int j = 1;
    Object localObject3 = "unknown";
    String str3 = "Unknown";
    int k = Integer.MIN_VALUE;
    String str1 = "Unknown";
    String str4 = getContext().getPackageName();
    Object localObject6 = getContext().getPackageManager();
    Object localObject5;
    String str2;
    int i;
    Object localObject1;
    if (localObject6 == null)
    {
      zzKl().zzLZ().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzatx.zzfE(str4));
      localObject5 = localObject3;
      str2 = str3;
      i = k;
      localObject3 = str1;
      this.mAppId = str4;
      this.zzbqv = ((String)localObject5);
      this.zzacM = str2;
      this.zzbsw = i;
      this.zzacL = ((String)localObject3);
      this.zzbsx = 0L;
      zzKn().zzLh();
      localObject1 = zzaba.zzaQ(getContext());
      if ((localObject1 == null) || (!((Status)localObject1).isSuccess())) {
        break label527;
      }
      i = 1;
      label137:
      if (i == 0) {
        zzbw((Status)localObject1);
      }
      if (i == 0) {
        break label650;
      }
      localObject1 = zzKn().zzLj();
      if (!zzKn().zzLi()) {
        break label532;
      }
      zzKl().zzMd().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      i = 0;
    }
    for (;;)
    {
      this.zzVX = "";
      this.zzbqz = 0L;
      zzKn().zzLh();
      try
      {
        localObject3 = zzaba.zzwQ();
        localObject1 = localObject3;
        if (TextUtils.isEmpty((CharSequence)localObject3)) {
          localObject1 = "";
        }
        this.zzVX = ((String)localObject1);
        if (i != 0) {
          zzKl().zzMf().zze("App package, google app id", this.mAppId, this.zzVX);
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        for (;;)
        {
          Object localObject2;
          label527:
          label532:
          zzKl().zzLZ().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzatx.zzfE(str4), localIllegalStateException);
          continue;
          i = 0;
        }
        this.zzbsy = 0;
        return;
      }
      if (Build.VERSION.SDK_INT >= 16) {
        if (zzade.zzbg(getContext()))
        {
          i = j;
          this.zzbsy = i;
          return;
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
            for (;;)
            {
              try
              {
                PackageInfo localPackageInfo = ((PackageManager)localObject6).getPackageInfo(getContext().getPackageName(), 0);
                localObject3 = str1;
                i = k;
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
                zzKl().zzLZ().zze("Error retrieving package info. appId, appName", zzatx.zzfE(str4), localObject5);
                Object localObject4 = localObject5;
                i = k;
                localObject5 = localObject2;
              }
              localIllegalArgumentException = localIllegalArgumentException;
              zzKl().zzLZ().zzj("Error retrieving app installer package name. appId", zzatx.zzfE(str4));
              continue;
              localObject2 = localObject3;
              if ("com.android.vending".equals(localObject3)) {
                localObject2 = "";
              }
            }
          }
          break;
          i = 0;
          break label137;
          if ((localObject2 != null) && (!((Boolean)localObject2).booleanValue()))
          {
            zzKl().zzMd().log("Collection disabled with firebase_analytics_collection_enabled=0");
            i = 0;
            continue;
          }
          if ((localObject2 == null) && (zzKn().zzwR()))
          {
            zzKl().zzMd().log("Collection disabled with google_app_measurement_enable=0");
            i = 0;
            continue;
          }
          zzKl().zzMf().log("Collection enabled");
          i = 1;
          continue;
        }
      }
      label650:
      i = 0;
    }
  }
  
  String zzmZ()
  {
    zzob();
    return this.zzacM;
  }
  
  long zzuW()
  {
    zzob();
    return 0L;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */